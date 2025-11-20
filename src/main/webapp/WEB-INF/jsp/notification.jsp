<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.QueueCountDTO" %>
<%@ page import="java.lang.reflect.Field" %>
<!-- Notifications Offcanvas -->
<script src="assets/js/sockjs.min.js"></script>
<script src="assets/js/stomp.min.js"></script>
<%
	Employee employee = new Employee();
	if (request.getAttribute("employee") != null) {
		employee = (Employee) request.getAttribute("employee");
	}
	int parameterCount = 0;
	QueueCountDTO queueCountDTO = null;
	if (request.getAttribute("queueCountDTO") != null) {
		queueCountDTO = (QueueCountDTO) request.getAttribute("queueCountDTO");

		Field[] fields = queueCountDTO.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (!field.isSynthetic()) {
				parameterCount++;
			}
		}
	}
%>
<div class="offcanvas offcanvas-end" tabindex="-1" id="notifications" aria-labelledby="notificationsLabel">
	<div class="offcanvas-header">
		<h5 class="offcanvas-title" id="notificationsLabel">Notifications</h5>
		<button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
	</div>
	<div class="offcanvas-body">
		<div id="notificationList" class="list-group mb-3">
			<!-- Notifications will be dynamically inserted here -->
		</div>
		<%--				<div id="allNotifications">--%>
		<%--					<!-- All notifications will be dynamically inserted here -->--%>
		<%--				</div>--%>
		<button id="loadMoreButton" class="btn btn-primary mt-3" onclick="loadMoreNotifications()">Load More</button>
		<div class="d-flex justify-content-center"><button id="markAsReadButton" onclick="markNotificationsAsRead()" class="btn btn-sm btn-secondary mt-3"><i class="ph-trash"></i> Mark as Read</button></div></div>
</div>

<!-- Notification Toast -->
<div class="position-fixed bottom-0 end-0 p-3" style="z-index: 11">
	<div id="notificationToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="true" data-bs-delay="5000">
		<div class="toast-header">
			<strong class="me-auto">New Notification</strong>
			<button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
		</div>
		<div class="toast-body">
			<!-- Toast content will be dynamically inserted here -->
		</div>
	</div>
</div>
<script>
	// Global variables
	var solid = '<%= employee.getJoinedSol() %>';
	var ppc = '<%= employee.getPpcno() %>';
	var currentPage = 0;
	const PAGE_SIZE = 20;
	var stompClient = null;

	// WebSocket connection
	function connectWebSocket() {
		var socket = new SockJS('ws');
		stompClient = Stomp.over(socket);

		stompClient.connect({}, function (frame) {
			console.log('Connected: ' + frame);
			stompClient.subscribe('/topic/sol/' + solid, function (message) {
				console.log('Received WebSocket message:', message.body);
				var notification = JSON.parse(message.body);
				console.log('Parsed notification:', notification);
				updateNotification(notification);
			});
		}, function (error) {
			console.error('WebSocket connection error:', error);
			setTimeout(connectWebSocket, 5000); // Reconnect after 5 seconds
		});
	}

	// Notification handling
	function updateNotification(notification) {
		console.log('Updating notification:', notification);
		updateNotificationCount();
		var listItem = createNotificationItem(notification);
		console.log('Created list item:', listItem);
		var notificationList = document.getElementById('notificationList');
		//	var allNotifications = document.getElementById('allNotifications');
		if (notificationList ) {
			notificationList.prepend(listItem);
			//	allNotifications.prepend(listItem.cloneNode(true));
		} else {
			console.error('Notification containers not found');
		}
		showToastNotification(notification);
	}
	setInterval(updateTimestamps, 60000);

	function createNotificationItem(notification) {
		var listItem = document.createElement('a');
		listItem.href = notification.url;
		listItem.className = 'list-group-item list-group-item-action';

		// Correctly format the timestamp using toLocaleString
		var formattedDate = new Date(notification.timestamp).toLocaleString();

		listItem.innerHTML = `
              <div class="d-flex w-100 justify-content-between">
            <h6 class="mb-1">`+notification.title+`</h6>
            <small data-timestamp="`+notification.timestamp+`">`+formattedDate+`</small>
        </div>
        <p class="mb-1">`+notification.subtitle+`</p>
        <small`+notification.category+`</small>
    `;

		return listItem;
	}


	function updateTimestamps() {
		var timestamps = document.querySelectorAll('[data-timestamp]');
		timestamps.forEach(function(element) {
			var timestamp = element.getAttribute('data-timestamp');
			var date = new Date(timestamp); // Use Date constructor to parse ISO string
			element.textContent = timeAgo(date);
		});
	}


	function timeAgo(date) {
		const seconds = Math.floor((new Date() - date) / 1000);
		const intervals = [
			{ label: 'year', seconds: 31536000 },
			{ label: 'month', seconds: 2592000 },
			{ label: 'day', seconds: 86400 },
			{ label: 'hour', seconds: 3600 },
			{ label: 'minute', seconds: 60 },
			{ label: 'second', seconds: 1 }
		];
		const interval = intervals.find(i => i.seconds < seconds);

		if (!interval) {
			return 'just now';
		}

		const count = Math.floor(seconds / interval.seconds);
		return count + ' ' + interval.label + (count !== 1 ? 's' : '') + ' ago';
	}


	function markNotificationsAsRead() {
		var ppc = '<%= employee.getPpcno() %>';  // Get the employee code (ppc)
		var solId = '<%= employee.getJoinedSol() %>';  // Get the solId (branch or solution identifier)

		// Prepare URL-encoded form data
		var formData = new URLSearchParams();
		formData.append("ppc", ppc);
		formData.append("solId", solId);

		fetch('notifications/markAsRead', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
				'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
			},
			body: formData
		})
				.then(response => response.json())  // Parse the JSON response (boolean)
				.then(success => {
					if (success) {
						// If marking was successful, reset the notification count and clear the list
						document.getElementById("notif_count").textContent = '0';  // Reset notification count
						document.getElementById('notificationList').innerHTML = '<p>No new Notification</p>';  // Clear the notification list
						showSuccessMessage('All notifications marked as read');
					} else {
						showErrorMessage('Failed to mark notifications as read. Please try again later.');
					}
				})
				.catch(error => {
					console.error('Error marking notifications as read:', error);
					showErrorMessage('Failed to mark notifications as read. Please try again later.');
				});
	}





	function loadInitialNotifications() {
		currentPage = 0;
		loadMoreNotifications();
	}

	function loadMoreNotifications() {
		var ppc = '<%= employee.getPpcno() %>';
		var solid = '<%= employee.getJoinedSol() %>';

		fetch(`notifications/paginated?page=`+currentPage+`&size=`+PAGE_SIZE+`&solId=`+solid+`&ppc=`+ppc, {
			headers: {


				'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
			}
		})
				.then(response => {
					if (!response.ok) throw new Error('Network response was not ok');
					return response.json();
				})
				.then(page => {
					const notifications = page.content;
					const notificationList = document.getElementById('notificationList');
					notifications.forEach(notification => {
						const listItem = createNotificationItem(notification);
						notificationList.appendChild(listItem);
					});
					if (page.last) {
						document.getElementById('loadMoreButton').style.display = 'none';
					} else {
						currentPage++;
					}
					updateNotificationCount();
					updateTimestamps();
				})
				.catch(error => {
					console.error('Error loading notifications:', error);
				});
	}


	function updateNotification(notification) {
		console.log('Updating notification:', notification);
		updateNotificationCount();
		var listItem = createNotificationItem(notification);
		console.log('Created list item:', listItem);
		var notificationList = document.getElementById('notificationList');
		//var allNotifications = document.getElementById('allNotifications');
		if (notificationList) {
			notificationList.prepend(listItem);
			//allNotifications.prepend(listItem.cloneNode(true));
			updateTimestamps(); // Update timestamps after adding new notification
		} else {
			console.error('Notification containers not found');
		}
		showToastNotification(notification);
	}


	// Toast notifications
	function showSuccessMessage(message) {
		showToast('Success', message, 'bg-success text-white');
	}

	function showErrorMessage(message) {
		showToast('Error', message, 'bg-danger text-white');
	}

	function showToastNotification(notification) {
		showToast('New Notification', notification.title, 'bg-primary text-white');
	}

	function showToast(title, message, className) {
		const toastElement = document.getElementById('notificationToast');
		if (!toastElement) {
			console.error('Toast element not found');
			return;
		}
		const toastBody = toastElement.querySelector('.toast-body');
		const toastTitle = toastElement.querySelector('.toast-header strong');

		toastTitle.textContent = title;
		toastBody.textContent = message;
		toastElement.className = `toast ${className}`;

		const toast = new bootstrap.Toast(toastElement);
		toast.show();
	}
	// Initialize
	document.addEventListener('DOMContentLoaded', function() {
		// Work Item search functionality
		function initializeWorkItemSearch() {
			const winumInput = document.getElementById("winum");
			const submitBtn = document.getElementById("submitBtn");
			const validationMessage = document.getElementById("validationMessage");

			function formatInput(input) {
				let value = input.value;
				const formattedPattern = /^VLR_\d{9}$/;
				if (formattedPattern.test(value)) {
					return;
				}
				let numericPart = value.replace(/\D/g, '');
				if (numericPart.length > 0) {
					value = 'VLR_' + numericPart.padStart(9, '0');
				}
				input.value = value;
			}

			function submitForm() {
				const winum = btoa(winumInput.value);
				if (winumInput.value.trim() === "") {
					validationMessage.style.display = "block";
				} else {
					validationMessage.style.display = "none";
					const form = document.createElement("form");
					form.method = "GET";
					form.action = "wisearch";
					const hiddenField = document.createElement("input");
					hiddenField.type = "hidden";
					hiddenField.name = "winum";
					hiddenField.value = winum;
					form.appendChild(hiddenField);
					document.body.appendChild(form);
					form.submit();
				}
			}

			winumInput.addEventListener('change', function() {
				formatInput(winumInput);
			});

			winumInput.addEventListener("keydown", function(event) {
				if (event.key === "Enter") {
					event.preventDefault();
					formatInput(winumInput);
					submitForm();
				}
			});

			submitBtn.addEventListener("click", function() {
				submitForm();
			});
		}

		console.log('DOM fully loaded and parsed');

		connectWebSocket();
		loadInitialNotifications();
		initializeWorkItemSearch();

		// Add event listener for "Mark as Read" button
		var markAsReadButton = document.getElementById('markAsReadButton');
		if (markAsReadButton) {
			markAsReadButton.addEventListener('click', markNotificationsAsRead);
		} else {
			console.error('Mark as Read button not found');
		}
	});
</script>