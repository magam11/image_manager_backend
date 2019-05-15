var TOKEN;
$(document).ready(function () {
    console.log("ready!");
    var tokenFromStorage = window.localStorage.getItem('token');
    if (tokenFromStorage != null) {
        initializeFirstPage(tokenFromStorage);
    }
});

function initializeFirstPage(token) {
    $.ajax({
        url: "http://localhost:8081/userInfo",
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

function login(phoneNumber, password) {
    var dataJson = {
        "phoneNumber": phoneNumber,
        "password": password
    }
    $.ajax({
        url: "http://localhost:8081/login",
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

