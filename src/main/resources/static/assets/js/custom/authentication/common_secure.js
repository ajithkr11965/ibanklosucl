document.addEventListener('DOMContentLoaded', function() {
    // Listen for storage events to detect logout
    window.addEventListener('storage', function(event) {
        if (event.key === 'vl-logout-event') {
            window.location.href = "logout";
        }
    });

    // Prevent F12, Ctrl+Shift+I, and right-click context menu
    document.addEventListener('keydown', function(event) {
        if (event.keyCode === 123 || (event.ctrlKey && event.shiftKey && event.keyCode === 73)) {
            event.preventDefault();
        }
        // Prevent backspace outside of inputs and contenteditable elements
        var target = event.target;
        if (event.keyCode === 8 && !(target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable)) {
            event.preventDefault();
        }
    });

    document.addEventListener('contextmenu', function(event) {
        event.preventDefault();
    });

    // Prevent back button navigation
    (function() {
        window.history.pushState(null, null, window.location.href);
        window.onpopstate = function() {
            window.history.pushState(null, null, window.location.href);
        };
    })();
});
