var TOKEN;
// var x=document.createElement('script');
// x.src='js/porc.js';
// document.body.appendChild(x);
// console.log(x);
// import a from 'js/porc.js';
$(document).ready(function () {
    var tokenFromStorage = window.localStorage.getItem('token');
    if (tokenFromStorage != null) {
        initializeFirstPage(tokenFromStorage);
    }
    $("#but_upload").click(function(){
        var fd = new FormData();
        var files = $('#image')[0].files[0];
        fd.append('picture',files);
        $.ajax({
            headers: {'Authorization': window.localStorage.getItem('token')},
            url: '/image/addImage',
            type: 'post',
            data: fd,
            contentType: false,
            processData: false,
            // success: function(response){
            //     alert('respons is ok');
            //     if(response != 0){
            //         $("#img").attr("src",response);
            //         $(".preview img").show(); // Display image element
            //     }else{
            //         alert('file not uploaded');
            //     }
            // },
        }).done(function (data) {
            if (data.success){
                alert('nkary pahpanvac e');
            }else {
                alert(data.message);
            }
        });
    });
});

function initializeFirstPage(token) {
    $.ajax({
        url: "/userInfo",
        method: "GET",
        contentType: 'application/json',
        headers: {'Authorization': token},
    }).done(function (data) {
        document.getElementById('login_div').style.display = "none";
        document.getElementById('bodyDiv').style.visibility = "visible";
        document.getElementById('logout_button').style.visibility = "visible";
        $("#user-name-label").html(data.userName);
        $("#user-phoneNumber-label").html(data.phoneNumber);
    });
}

function login() {
    var phoneNumber=document.getElementById('phoneNumberInput').value;
    var password = document.getElementById('passwordInput').value;
    var dataJson = {phoneNumber,password};
    $.ajax({
        url: "/login",
        method: "POST",
        contentType: 'application/json',
        data: JSON.stringify(dataJson),
    }).done(function (data) {
        if (data.success) {
            document.getElementById('login_div').style.display = "none";
            var token = data.token;
            if ($("#remember").is(":checked")) {
                TOKEN = token;
                window.localStorage.setItem('token', token);
                initializeFirstPage(token)
            } else {
                TOKEN = token;
                initializeFirstPage(TOKEN);
            }
        } else {
            var failureLoginText_label = document.getElementById('failureloginText_label');
            failureLoginText_label.style.visibility = "visible";
            failureLoginText_label.style.color = "red";
            failureLoginText_label.innerHTML = data.message;
        }
    }).fail(function(data){
        alert(data)
    });
}



function logout() {
    history.go(0);
    window.localStorage.clear();
    history.go(0);
    // $.ajax({
    //     url: "http://localhost:8081/",
    //     method: "GET",
    //     contentType: 'application/json',
    // }).done(function (data) {
    //     $(document.body).html(data)
    // });
}

