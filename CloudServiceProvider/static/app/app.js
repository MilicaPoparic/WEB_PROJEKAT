const Home = { template: '<home-page></home-page>' }
const Login = { template: '<log-in></log-in>' }
const Org = { template: '<organization></organization>' }

const router = new VueRouter({
	  mode: 'hash',
	  routes: [
	    { path: '/', component: Login},
	    { path: '/h', component: Home},
	    { path: '/o', component: Org}
	  ]
});

var app = new Vue({
	router,
	el: '#cloud'
});

