<%--
  Created by IntelliJ IDEA.
  User: SIBL11965
  Date: 06-08-2024
  Time: 11:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>iBankLOS - Error Page</title>
   	<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
		<link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
    <script src="assets/js/jquery/jquery.min.js"></script>
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background: linear-gradient(to bottom right, #a3cce4, #dbe9f5);
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            overflow: hidden;
            color: #333;
            position: relative;
        }
        .particle-container {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            overflow: hidden;
            pointer-events: none;
            z-index: 0;
        }
        .particle {
            position: absolute;
            background: rgba(255, 255, 255, 0.3);
            border-radius: 5px;
            backdrop-filter: blur(10px);
            box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
            animation: float 15s ease-in-out infinite;
            transition: background-color 0.3s;
        }
        .particle:hover {
            background: rgba(255, 215, 0, 0.5); /* Gold-like hover effect */
        }
        @keyframes float {
            0% {
                transform: translateY(100vh) translateX(0);
                opacity: 1;
            }
            70% {
                opacity: 1;
            }
            100% {
                transform: translateY(-100vh) translateX(-50px);
                opacity: 0;
            }
        }
        .error-container {
            text-align: center;
            background-color: rgba(255, 255, 255, 0.9);
            padding: 50px;
            box-shadow: 0 0 30px rgba(0,0,0,0.1);
            border-radius: 15px;
            max-width: 600px;
            width: 90%;
            position: relative;
            z-index: 1;
        }
        .error-container img {
            max-width: 120px;
            margin-bottom: 20px;
        }
        .error-container h1 {
            color: #007bff;
            margin-bottom: 10px;
            font-size: 28px;
        }
        .error-container p {
            margin: 10px 0;
            font-size: 16px;
            color: #666;
        }
        .error-container a, .error-container button {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 25px;
            color: white;
            background-color: #007bff;
            text-decoration: none;
            border-radius: 5px;
            font-size: 14px;
            transition: background-color 0.3s, transform 0.2s;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .error-container a:hover, .error-container button:hover {
            background-color: #0056b3;
            transform: translateY(-3px);
        }
        .error-container button {
            border: none;
            cursor: pointer;
        }
        .quote {
            font-style: italic;
            color: #888;
            margin-top: 30px;
        }
        .contact-form {
            margin-top: 20px;
            text-align: left;
            display: none;
        }
        .contact-form input, .contact-form textarea {
            width: calc(100% - 20px);
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            outline: none;
            resize: vertical;
            transition: border-color 0.3s;
        }
        .contact-form input:focus, .contact-form textarea:focus {
            border-color: #007bff;
        }
        .contact-form textarea {
            height: 100px;
        }
        .navigation-suggestions {
            margin-top: 20px;
            font-size: 14px;
        }
        .navigation-suggestions a {
            color: #007bff;
            text-decoration: none;
            transition: color 0.3s;
        }
        .navigation-suggestions a:hover {
            color: #0056b3;
        }
        .branding {
            margin-top: 20px;
            font-size: 18px;
            color: #555;
            font-weight: 700;
        }
    </style>
</head>
<body id="kt_body" class="header-tablet-and-mobile-fixed aside-enabled" style="background-color: #ffffff">
    <div class="particle-container">
        <div class="particle" style="width: 80px; height: 80px; bottom: -70px; left: 10%; animation-delay: 0s;"></div>
        <div class="particle" style="width: 100px; height: 100px; bottom: -100px; left: 30%; animation-delay: 2s;"></div>
        <div class="particle" style="width: 120px; height: 120px; bottom: -130px; left: 50%; animation-delay: 4s;"></div>
        <div class="particle" style="width: 70px; height: 70px; bottom: -50px; left: 70%; animation-delay: 6s;"></div>
        <div class="particle" style="width: 90px; height: 90px; bottom: -90px; left: 90%; animation-delay: 8s;"></div>
    </div>
    <div class="kt error-container">
        <h1>Something Went Wrong</h1>
        <p><%= request.getAttribute("error") %></p>
        <a href="dashboard" class="btn btn-lg btn-danger">Back to Home</a>
        <div class="contact-form" id="contactForm">
            <input type="text" placeholder="Your Name" required>
            <input type="email" placeholder="Your Email" required>
            <textarea placeholder="Describe the issue..."></textarea>
            <button type="submit">Send</button>
        </div>


        <div class="navigation-suggestions">
            <p>Here are some useful links:</p>
            <a href="/faq" class="btn btn-sm btn-primary">FAQ</a> |
            <a href="/support" class="btn btn-sm btn-primary">Support</a> |
            <a href="#" class="btn btn-sm btn-primary" onclick="toggleContactForm()">Report Issue</a>
        </div>
        <div class="branding">iBankLOS</div>
    </div>
    <script>
        function toggleContactForm() {
            const contactForm = document.getElementById('contactForm');
            contactForm.style.display = contactForm.style.display === 'none' ? 'block' : 'none';
        }
    </script>
</body>
</html>
