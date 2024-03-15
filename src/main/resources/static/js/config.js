
// 创建axios实例
window.axiosInstance = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 20000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 添加请求拦截器
window.axiosInstance.interceptors.request.use(
    function (config) {
        // 从cookie中获取token
        let token = document.cookie.replace(/(?:(?:^|.*;\s*)token\s*\=\s*([^;]*).*$)|^.*$/, "$1");
        config.headers.Authorization = `Bearer ${token}`;
        return config;
    }, function (error) {
        // 对请求错误做些什么
        return Promise.reject(error);
    });

// 添加响应拦截器
window.axiosInstance.interceptors.response.use(
    function (response) {
        let status= response.status;
        if(status === 200){
             return response.data;
        } else{
            return Promise.reject(response.data);
        }
    }, function (error) {
        // 对响应错误做点什么
        return Promise.reject(error);
    });