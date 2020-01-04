const Home = { template: '<home-page></home-page>' }
const Login = { template: '<log-in></log-in>' }

const router = new VueRouter({
	  mode: 'hash',
	  routes: [
	    { path: '/', component: Login},
	    { path: '/h', component: Home}
	  ]
});

var app = new Vue({
	router,
	el: '#cloud'
});

