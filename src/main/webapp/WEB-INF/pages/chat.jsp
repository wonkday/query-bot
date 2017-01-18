<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    <meta name="viewport" content="initial-scale=1, maximum-scale=1">
    <script type="text/javascript" src="${pageContext.request.contextPath}/webjars/jquery/2.1.3/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.2-2/js/bootstrap.min.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Home</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel='stylesheet' href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.2-2/css/bootstrap.min.css'>
    <script>
        /*jQuery(document).ready(function($)
        {
            $("#header").load("${pageContext.request.contextPath}/static/css/header.jsp");
            $("#footer").load("${pageContext.request.contextPath}/static/css/footer.html");
        });*/

        Number.prototype.padLeft = function(base,chr){
           var  len = (String(base || 10).length - String(this).length)+1;
           return len > 0? new Array(len).join(chr || '0')+this : this;
        }

        $(function () {
            $("#sendBtn").on("click", function () {
                submitToServer();
            });
        });

        $(document).on("submit", "#myform", function(event) {
            //var $form = $(this);
            event.preventDefault(); // Important! Prevents submitting the form.

			///-----------------
			var form = $(this).parent();
			// Get form fields
			var data = $(form).serializeArray(), obj = {}, j = 0;
			for (var i = 0; i < data.length; i++)
			{
			  if( data[i].name in obj )
			  {
				var key = data[i].name + '_' + j;
				obj[key] = data[i].value;
				j++;
			  }
			  else
			  {
				obj[data[i].name] = data[i].value;
			  }
			};
			///------------------

			submitToServer();
        });


        function submitToServer()
        {
            var inputData = $("#instrMsg").val();
            //alert("Val:" + inputData);
            if(inputData != null && inputData.trim().length > 0)
            {
                addMessageToChatSession(inputData,"_h");
                //send to server
                $.ajax({
                        url: '${pageContext.request.contextPath}/chat',
                        type: 'POST',
                        data: {'chatReq':inputData}, // An object with the key 'chatReq' and value 'inputData';
                        success: function (result) {
                          //alert("response here...." + result);
                          //console.log(JSON.stringify(result));
                          addMessageToChatSession(result.chatResp,"_r");
                          $("#instrMsg").val("");
                        }
                    });
            }
            else{
                alert("Input cant be empty!");
            }
        }

        function addMessageToChatSession(responseData,type)
        {
			//alert("exec - addMessageToChatSession(): " + responseData);
			var d = new Date();
			var millis = Date.now();
			var userTag = "";
			var alignTxt = "right";
			var marginTxt = "margin: 0 10px 15px 0;"
			if(type == '_h')
			{
				userTag = '<h2>Me</h2> ';
				alignTxt = "right";
				marginTxt = "margin: 0 10px 15px 10px;"
			}
			else
			{
				userTag = '<h3>Bot</h3> ';
				alignTxt = "left";
				marginTxt = "margin: 0 0 10px 10px;"
			}
            var messageDiv = '<div class="message'+ type +'" id="message' + millis
							  +'"> <img style="border-radius: 100%;' + marginTxt + ' float:' + alignTxt +';" src="${pageContext.request.contextPath}/static/img/icon.png"/>'
							  + userTag
                              + '<p>' + responseData +'</p>'
                              + '<p class="time' + type + '"><span class="entypo-clock"></span>'
                              + [(d.getMonth()+1).padLeft(),d.getDate().padLeft(),d.getFullYear()].join('/')
                              + ' ' + [ d.getHours().padLeft(),d.getMinutes().padLeft(),d.getSeconds().padLeft()].join(':')
                              + '</p> </div>';
            $("#messages").append(messageDiv);

			//auto-scroll to bottom after data is added
			var elem = document.getElementById('message'+millis);
			var footer = document.getElementById('panel-footer');
			//elem.scrollTop = elem.scrollTop + elem.scrollHeight + footer.scrollHeight;
			document.scrollingElement.scrollTop = document.scrollingElement.scrollTop + elem.scrollHeight + footer.scrollHeight;
        }


    </script>
    <title>Chat Bot</title>
    <style>
        @import url("${pageContext.request.contextPath}/static/css/Roboto.css");
        @import url("${pageContext.request.contextPath}/static/css/entypo.css");
        [class*="entypo-"]:before {
          font-family: 'entypo', sans-serif;
        }
        * {
          box-sizing: border-box;
          margin: 0;
        }
        body {
          background: #BDC1C6;
          font-family: 'Roboto';
        }
        p {
          font-weight: 300;
        }
        .chat {
          width: 90%;
          background: #fff;
          margin: 0 auto;
        }
        header {
          background: #35323C;
          height: 50px;
          padding: 5px 10px;
        }
        .menu-icon {
          background: #2F2E33;
          padding: 5px 5px;
          float: left;
          font-size: 3em;
          line-height: 0.5em;
          color: #fff;
          border-radius: 3px;
        }
        .menu-icon:hover {
          background: #39caad;
          cursor: pointer;
        }
        h1 {
          float: right;
          color: #fff;
          margin: 5px;
          font-weight: 300;
          font-size: 1.3em;
        }
        .new {
          background: rgb(57, 202, 173);
          color: #fff;
          text-align: center;
          padding: 10px;
        }
        .new:hover {
          cursor: pointer;
          background: rgba(57, 202, 173, 0.9);
        }
        .messages {
          padding: 0px;
          height: 70%;
          overflow:auto;
        }
        .message_h {
		  text-align: right;
          float: right;
          width: 100%;
          margin: 10px 0;
          border-bottom: 1px solid #ccc;
        }
        .message p {
          font-size: 0.8em;
          width: 90%;
          margin: 5px 0;
        }
		.message_r {
		  text-align: left;
          float: left;
          width: 100%;
          margin: 10px 0;
          border-bottom: 1px solid #ccc;
        }
        img {
          border-radius: 100%;
          float: left;
          margin: 0 10px 15px 10px;
        }
        p.time_h {
          color: rgba(0,0,0,0.5);
          font-weight: 400;
        }
		p.time_r {
          color: rgba(0,0,0,0.5);
          font-weight: 400;
		  padding-left: 4em;
        }
        h2 {
          font-size: 1em;
          font-weight: 400;
		  float: right;
		  margin: 10px 0 10px 30px;
        }
        h2:after {
          content: '';
          display: inline-block;
          height: 10px;
          width: 10px;
          background: #39caad;
          border-radius: 100%;
          margin-left: 5px;
        }
		h3 {
          font-size: 1em;
          font-weight: 400;
		  float: left;
		  margin: 10px 30px 0 10px;
        }
		h3:before {
          content: '';
          display: inline-block;
          height: 10px;
          width: 10px;
          background: #39caad;
          border-radius: 100%;
          margin-right: 10px;
        }
        tab { padding-left: 4em; }
    </style>
    </head>
    <body>
        <div id="header"></div>
        <div id="wrap">
            <!-- page contents start here -->
            <form:form id="myform" method="POST" commandName="chat" action="${pageContext.request.contextPath}/chat">
			<!--form id="myform" method="POST" commandName="chat" action="${pageContext.request.contextPath}/chat"-->
              <div align="center">
                <span id="loading">&nbsp;&nbsp;&nbsp;&nbsp;<!-- spinner --></span>
                <div class="chat">
                  <header>
                    <div class="menu-icon"><span class="entypo-menu"></span></div>
                    <h1>Query BOT</h1>
                  </header>
                  <div class="new">
                    <p><span class="entypo-feather"></span> Messages</p>
                  </div>
                  <section class="messages" id="messages">

                  </section>
                    <div class="panel-footer" id="panel-footer">
                        <div class="input-group">
                		  <input id="instrMsg" type="text" class="form-control">
                		  <span class="input-group-btn">
                			<button class="btn btn-default" id="sendBtn" type="button">Send</button>
                		  </span>
                		</div>
                	</div>
                </div>
              </div>
            <!--/form -->
			</form:form>
            <!-- page contents end here -->
        </div>
        <div id="footer"></div>
    </body>
</html>