document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();
    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username: username, password: password }),
    })
        .then(response => response.json())
        .then(data => {
            if (data.token) {
                document.cookie = `token=${data.token}`;
                window.location.href = 'index';
            } else {
                document.getElementById('error-message').textContent = '用户名或密码错误';
            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });
});




