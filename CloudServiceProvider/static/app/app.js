const Home = { template: '<home-page></home-page>' }
const Login = { template: '<log-in></log-in>' }
const Org = { template: '<organization></organization>' }
const Categ = { template: '<categ></categ>' }
const AddCateg = { template: '<addCateg></addCateg>' }
const DetailCateg = { template: '<detailCateg></detailCateg>' }
const AddOrg = { template: '<add-org></add-org>' }
const ChangeOrg = { template: '<change-org></change-org>' }
const Forbidden = { template: '<forbidden></forbidden>' }
const Drives = { template: '<drive></drive>' }
const AddDrive ={template: '<addDr></addDr>'}
const DetailDrive ={template: '<detailDr></detailDr>'}
const searchDrive ={template: '<searchDr></searchDr>'}

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
	    { path: '/forbidden', component: Forbidden},
	    { path: '/drives', component: Drives},
	    { path: '/ad', component: AddDrive},
	    { path: '/detailDrive', component: DetailDrive},
	    { path: '/resultSearchingDrive', component: searchDrive}
	  ]
});

var app = new Vue({
	router,
	el: '#cloud'
});

