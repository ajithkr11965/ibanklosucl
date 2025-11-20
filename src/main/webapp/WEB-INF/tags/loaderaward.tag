<!DOCTYPE html>
<html style="height: 100%">
<head>
    <style>

        :root {
            --gold-primary: #FFD700;
            --gold-secondary: #DAA520;
            --gold-highlight: #FFF1BD;
            --gold-shadow: #B8860B;
        }

        .bodyaw {
            /*! height: 100%; */
            /*! margin: 0; */
            /*! display: flex; */
            /*! justify-content: center; */
            /*! align-items: center; */
            background: linear-gradient(135deg,
            #ffffff,
            rgba(255, 215, 0, 0.05),
            rgba(255, 215, 0, 0.08)
            );
            /*! background-size: 400% 400%; */
            /*! animation: gradientShift 15s ease infinite; */
            font-family: 'Inter', sans-serif;
            width: 100% !important;
            height: 100% !important;
            z-index: 1000;
            background-color: rgba(255, 255, 255, 0.9);
            background-repeat: no-repeat;
            background-position: center;
            display: flex;
            justify-content: center;
            align-items: center;
            position: fixed;
        }

        .loader-container-aw {
            position: relative;
            width: 400px;
            height: 550px;
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 1rem;
            animation: fadeIn 1.5s ease-in-out, scaleUp 2s ease-in-out;
        }

        .award-container {
            position: relative;
            width: 495px;
            height: 495px;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .award-image {
            position: relative;
            top: -16px;
            width: 300px;
            height: 300px;
            z-index: 3;
            object-fit: contain;
            transform: translateX(-5%);
            transition: transform 0.3s ease;
        }

        .award-image:hover {
            transform: scale(1.5) rotate(1deg);
        }

        .loader-aw {
            position: absolute;
            width: 288px;
            height: 288px;
            z-index: 1;
            animation: complexRotation 6s cubic-bezier(0.45, 0, 0.55, 1) infinite;
        }

        .arc {
            position: absolute;
            width: 100%;
            height: 100%;
            border-radius: 50%;
            border: 3px solid transparent;
        }

        .arc-1 {
            border-top: 3px solid var(--gold-primary);
            border-left: 3px solid var(--gold-highlight);
            animation: complexRotation 6s cubic-bezier(0.45, 0, 0.55, 1) infinite;
            box-shadow: 0 0 15px rgba(255, 215, 0, 0.3);
        }

        .arc-2 {
            width: 95%;
            height: 95%;
            top: 2.5%;
            left: 2.5%;
            border-right: 3px solid var(--gold-secondary);
            border-bottom: 3px solid var(--gold-shadow);
            animation: reverseRotation 6s linear infinite;
            animation-delay: 0.5s;
        }

        .arc-3 {
            width: 90%;
            height: 90%;
            top: 5%;
            left: 5%;
            border: 2px solid var(--gold-primary);
            opacity: 0.3;
            animation: pulseArc 6s ease-in-out infinite;
            animation-delay: 1s;
        }

        .ring-highlight {
            position: absolute;
            width: 100%;
            height: 100%;
            border-radius: 50%;
            background: linear-gradient(135deg,
            rgba(255, 215, 0, 0.15),
            rgba(255, 215, 0, 0.05)
            );
            filter: blur(10px);
            animation: rotateHighlight 8s linear infinite, glowPulse 4s ease-in-out infinite;
        }

        .award-description {
            position: relative;
            margin-top: 20px;
            width: 100%;
            text-align: center;
            color: #000000;
            font-family: 'Inter', sans-serif;
            font-size: 18px;
            font-weight: 600;
            text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.3);
        }

        /* SVG Loader */
        #svg-loader-aw {
            width: 300px;
            height: 200px;
            margin-top: 10px;
        }

        #outline {
            stroke: var(--gold-primary);
            fill: none;
            stroke-width: 4;
            stroke-linecap: round;
            stroke-linejoin: round;
            stroke-miterlimit: 10;
            stroke-dasharray: 242.78, 242.78;
            stroke-dashoffset: 0;
            animation: svgAnim 1.6s linear infinite;
        }

        @keyframes svgAnim {
            0% {
                stroke-dasharray: 0, 242.78;
                stroke-dashoffset: 0;
            }
            50% {
                stroke-dasharray: 120, 242.78;
                stroke-dashoffset: -120;
            }
            100% {
                stroke-dasharray: 0, 242.78;
                stroke-dashoffset: -242.78;
            }
        }

        @keyframes complexRotation {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        @keyframes reverseRotation {
            from { transform: rotate(360deg); }
            to { transform: rotate(0deg); }
        }

        @keyframes pulseArc {
            0%, 100% { transform: scale(1); opacity: 0.3; }
            50% { transform: scale(1.05); opacity: 0.5; }
        }

        @keyframes rotateHighlight {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }

        @keyframes gradientShift {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        @keyframes scaleUp {
            from { transform: scale(0.8); }
            to { transform: scale(1); }
        }

        .hidden{
            display:none !important;
        }
    </style>
</head>
<div class="bodyaw" id="loader-container-aw">
<div class="loader-container-aw" >
    <div class="award-container">
        <div class="loader-aw">
            <div class="ring-highlight"></div>
            <div class="arc arc-1"></div>
            <div class="arc arc-2"></div>
            <div class="arc arc-3"></div>
        </div>
        <div class="background-glow"></div>
        <img src="static/assets/images/Award.png" alt="Award" class="award-image">
    </div>
    <div class="award-description">
        Driving Innovation in Digital Lending Excellence
    </div>
    <!-- SVG Loader Replacing Progress Bar -->
    <svg id="svg-loader-aw" viewBox="0 0 187.3 93.7" preserveAspectRatio="xMidYMid meet">
        <path id="outline" d="M93.9,46.4c9.3,9.5,13.8,17.9,23.5,17.9s17.5-7.8,17.5-17.5s-7.8-17.6-17.5-17.5c-9.7,0.1-13.3,7.2-22.1,17.1
                c-8.9,8.8-15.7,17.9-25.4,17.9s-17.5-7.8-17.5-17.5s7.8-17.5,17.5-17.5S86.2,38.6,93.9,46.4z" />
    </svg>
</div>
</div>
<script>
  //  var loaders = document.querySelectorAll('.loader-container-aw');
    var loadersparent = document.getElementById('loader-parent');
    var activeLoader = null;
    var loaders = document.getElementById("loader-container-aw");
    var loadinit = document.getElementById("loadinit-aw");
    function showLoader() {
        loaders.classList.remove("hidden");
    }

    function hideLoader() {
        loaders.classList.add("hidden");
    }
    window.onload= function(){
        var time=100;
        if(loadinit){
            time=2000;
        }
        var timer=setTimeout(function()
        {
            hideLoader();
            clearTimeout(timer);
        },time);
    };
    showLoader();


</script>

</html>
