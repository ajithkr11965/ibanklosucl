/* ------------------------------------------------------------------------------
 *
 *  # Progress bars & loaders
 *
 *  Demo JS code for components_progress.html page
 *
 * ---------------------------------------------------------------------------- */


// Setup module
// ------------------------------

const CustomProgress = function() {


    //
    // Setup module components
    //

    // Elements
    // Change button.getAttribute('data-icon') to your desired icon here. Current
    // config is for demo. Or use this code if you wish
    const buttonClass = 'save-button',
        containerClass = 'card',
        overlayClass = 'card-overlay',
        overlayAnimationClass = 'card-overlay-fadeout';
    // Spinner with overlay
    const _componentOverlay = function() {
        // Configure
        document.querySelectorAll(`.${buttonClass}`).forEach(function(button) {
            button.addEventListener('click', function(e) {
                e.preventDefault();

                // Elements
                const parentContainer = button.closest(`.${containerClass}`),
                    overlayElement = document.createElement('div'),
                    overlayElementIcon = document.createElement('span');

                // Append overlay with icon
                overlayElement.classList.add(overlayClass);
                parentContainer.appendChild(overlayElement);
                if(button.getAttribute('data-spin') == 'false') {
                    overlayElementIcon.classList.add(button.getAttribute('data-icon'));
                }
                else {
                    overlayElementIcon.classList.add(button.getAttribute('data-icon'), 'spinner');
                }
                overlayElement.appendChild(overlayElementIcon);
            });
        });
    };

    const _componentOverLayHide = function() {
        // Configure
        document.querySelectorAll(`.${buttonClass}`).forEach(function(button) {
            // Element
            const containerElement = button.closest(`.${containerClass}`);
            if (containerElement) {
                const overLayElement = containerElement.querySelector(`.${overlayClass}`);
                if (overLayElement) {
                    overLayElement.classList.add(overlayAnimationClass);
                    ['animationend', 'animationcancel'].forEach(function(e) {
                        overLayElement.addEventListener(e, function() {
                            overLayElement.remove();
                        });
                    });
                }
            }
        });
    }


    //
    // Return objects assigned to module
    //

    return {
        init: function() {
            _componentOverlay();
        },
        hide:function (){
            _componentOverLayHide();
        }
    }
}();


// Initialize module
// ------------------------------

document.addEventListener('DOMContentLoaded', function() {
    CustomProgress.init();
    CustomProgress.hide();
});
