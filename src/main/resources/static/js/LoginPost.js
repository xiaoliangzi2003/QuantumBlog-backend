import axios from 'axios';

document.getElementById('login-form').addEventListener('submit', function(e) {
    e.preventDefault();

    var account = document.getElementById('account').value;
    var password = document.getElementById('password').value;

    var loginRequest = {
        account: {
            account: account,
            password: password
        }
    };

    axios.post('http://localhost:8080/login', loginRequest,{
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(function (response) {
        console.log(response);
    })
        .catch(function (error) {
            console.log(error);
        });
});