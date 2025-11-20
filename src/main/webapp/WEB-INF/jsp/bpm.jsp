
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Split Pane Layout</title>
<style>
    .container {
        display: flex;
    }
    .left-pane {
        width: 50%;
        padding: 20px;
        border-right: 1px solid #ccc;
    }
    .right-pane {
        width: 50%;
    }
    iframe {
        width: 100%;
        height: 600px; /* Adjust the height based on your requirement */
        border: none;
    }
</style>
</head>
<body>
    <div class="container">
        <div class="left-pane">
            <!-- Add your form or data entry elements here -->
            <form action="submitData.jsp">
                <label for="dataInput">Enter Data:</label>
                <input type="text" id="dataInput" name="data">
                <button type="submit">Submit</button>
            </form>
        </div>
        <div class="right-pane">
            <!-- Replace 'your-url-here' with the actual URL you want to display -->
            <iframe src="https://infobankuat.sib.co.in/omnidocs/WebApiRequestRedirection?Application=foldview&cabinetName=sibcab&sessionIndexSet=false&Userdbid=-186214285&FolderName=CHARGE_EXEMPT_LOAN__128&enableDCInfo=true&s=s&OD_UID=3025671671842755292" frameborder="0" class="w-100 h-100"></iframe>
        </div>
    </div>
</body>
</html>
