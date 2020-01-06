const Home = { template: '<home-page></home-page>' }
const Login = { template: '<log-in></log-in>' }
const Org = { template: '<organization></organization>' }
const Categ = { template: '<categ></categ>' }
const AddCateg = { template: '<addCateg></addCateg>' }
const DetailCateg = { template: '<detailCateg></detailCateg>' }
const AddOrg = { template: '<add-org></add-org>' }
const ChangeOrg = { template: '<change-org></change-org>' }

const router = new VueRouter({
	  mode: 'hash',
	  routes: [
	    { path: '/', component: Login},
	    { path: '/h', component: Home},
	    { path: '/o', component: Org},
	    { path: '/c', component: Categ},
	    { path: '/ac', component: AddCateg},
	    { path: '/d', component: DetailCateg},
	    { path: '/addOrg', component: AddOrg},
	    { path: '/changeOrg', component: ChangeOrg},
	  ]
});

var app = new Vue({
	router,
	el: '#cloud'
});

