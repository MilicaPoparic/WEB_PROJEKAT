
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
const Users = {template: '<users></users>'}
const AddUser = {template: '<add-user></add-user>'}
const ChangeUser = {template: '<change-user></change-user>'}
const Profile = {template: '<profile-info></profile-info>'}
const ChangeVm = { template: '<change-vm></change-vm>' }
const SearchVM= {template: '<search-vm></search-vm>'}
const AddVM= {template: '<add-vm></add-vm>'}

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
	    { path: '/resultSearchingDrive', component: searchDrive},
	    { path: '/users', component: Users},
	    { path: '/addUser', component: AddUser},
	    { path: '/changeUser', component: ChangeUser},
	    { path: '/profile', component: Profile},
	    { path: '/changeVM', component: ChangeVm},
	    { path: '/searchV', component: SearchVM},
	    { path: '/addVM', component: AddVM}
	  ]
});

var app = new Vue({
	router,
	el: '#cloud'
});

