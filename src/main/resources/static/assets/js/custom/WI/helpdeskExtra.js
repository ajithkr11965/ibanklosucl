function setupEventListeners() {
    document.querySelectorAll('#apiLogsTable tbody tr').forEach(function(row) {
        row.addEventListener('mouseover', function() {
            var copyIcon = this.querySelector('.copy-icon');
            if (copyIcon) copyIcon.style.opacity = '1';
        });
        row.addEventListener('mouseout', function() {
            var copyIcon = this.querySelector('.copy-icon');
            if (copyIcon) copyIcon.style.opacity = '0.6';
        });
    });
}

function checkSessionValidity() {
    var currentTime = new Date().getTime();
    var sessionTimeout = 30 * 60 * 1000;
    if (currentTime - pageLoadTime > sessionTimeout) {
        showError('Your session has expired. Please refresh the page.');
        return false;
    }
    return true;
}

function copyToClipboard(selector, button) {
    var content = document.querySelector(selector).textContent;
    var tooltip = bootstrap.Tooltip.getInstance(button);

    navigator.clipboard.writeText(content)
        .then(function() {
            button.setAttribute('data-bs-original-title', 'Copied!');
            tooltip.show();
            setTimeout(function() {
                button.setAttribute('data-bs-original-title', 'Copy to clipboard');
                tooltip.hide();
            }, 2000);
        })
        .catch(function(error) {
            console.error('Failed to copy:', error);
            showError('Failed to copy to clipboard');
        });
}
