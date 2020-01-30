Vue.component("home-page", {
	data: function(){
		return{
			virtMachines: {},
			pretraga: "",
			role: "",
			name:'',
			fromm:'',
			too:'',
			fromm1:'',
			too1:'',
			fromm2:'',
			too2:'',
			error1:''
		}
	},
	template: ` 
<div>
		
	<p>Virtual machines</p>
	<table border="1" class="table">
		<tr bgcolor="#f2f2f2">
			<th> Name </th>
			<th> Core </th>
			<th> RAM </th>
			<th> GPU </th>
			<th> Organization </th>
		</tr>
		
		
		<tr v-for="m in virtMachines">
			<td><a href="#" v-on:click="showDetails(m)">{{m.name}}</a></td>
			<td>{{m.category.coreNumber}}</td>
			<td>{{m.category.RAM}}</td>
			<td>{{m.category.GPUcores}}</td>
			<td>{{m.nameOrg}}</td>
		</tr>
	</table>
	<br>
	<button v-on:click="dodajVM()" v-if="role!='user'">Add new vm</button>
	<br> <br>
	Find a VM:
	<table border="1">
		<tr>
			<td> Name: </td>
			<td><input type="text" v-model="name" name="name"></td>
		</tr>
		
		<tr>
			<td> Number of CPU: </td>
			<td><input type="number" style="width:60px" size="5" v-model="fromm" name="fromm">
			-
			<input type="number" style="width:60px" size="5" v-model="too" name="too"></td>
		</tr>
		<tr>
		   <td> Number of RAM: </td>
			<td><input type="number" style="width:60px" size="5" v-model="fromm1" name="fromm1">
			-
			<input type="number" style="width:60px" size="5" v-model="too1" name="too1"></td>
		</tr>
		<tr>
			<td> Number of GPU: </td>
			<td><input type="number" style="width:60px" size="5" v-model="fromm2" name="fromm2">
			-
			<input type="number" style="width:60px" size="5" v-model="too2" name="too2"></td>
		</tr>
			
	</table>
	{{error1}}
	 <p>
		<button v-on:click="filter()">Filter</button>
	</p>
	 
	<a href="#/profile">Profile</a> <br>
	<a href="#/drives">Drives</a> <br>
	<a href="#/o" v-if="role=='superAdmin' || role=='admin'">Organizations</a> <br>
	<a href="#/users" v-if="role=='superAdmin' || role=='admin'">Users</a> <br>
	<a href="#/c" v-if="role=='superAdmin'">Categories</a> <br>
	<a href="#/report" v-if="role=='admin'">Mothly report</a> <br>
	<button v-on:click="logout">Logout</button> <br>
	
	
</div>		  
`
	, 
	methods : {
		filter : function() {
			if(!this.name && !this.fromm && !this.too && !this.fromm1 && !this.too1  && !this.fromm2 && !this.too2){
				alert("Unesite parametre pretrage");
			}
			else{
				 this.error1 ='';
				if(!this.name){
					this.name=null;
				}if(!this.fromm){
					this.fromm=0;
				}if(!this.too){
					this.too=0;
				}
				if(!this.fromm1){
					this.fromm1=0;
				}if(!this.too1){
					this.too1=0;
				}
				if(!this.fromm2){
					this.fromm2=0;
				}if(!this.too2){
					this.too2=0;
				}
				let vm ={
					"name":this.name, "fromm":this.fromm, "too":this.too,"fromm1":this.fromm1, "too1":this.too1,"fromm2":this.fromm2,
					"too2":this.too2
				}
				axios
			      .post('rest/filterVM', vm)
			      .then((response) => {
			    	  if(response.status == 200) {
			    		  this.error1 ='';
			    		  location.href = '#/searchV';
			    	  }
			      })
			      .catch((response)=>{
			    	  this.error1 ='No result of searching!';
			      })
			    
			}
		},
		dodajVM : function() {
			axios
		      .post('rest/addVM', "")
		      .then(response => location.href = '#/addVM');
		},
		logout : function() {
			axios
		      .post('rest/logout', "nesto")
		      .then(response => location.href = '#/');

		},
		showDetails : function(m) {
			axios
		      .post('rest/captureVM', m)
		      .then(response => location.href = '#/changeVM');
		}
		
	},
	mounted () {
        axios
          .get('rest/testLogin')
          .then((response) => {
			    	  if(response.status == 200) {
			    		  location.href = '#/h';
			    	  }
			      })
			      .catch((response)=>{
			    	  location.href = '#/';
			      })
        axios
         .get('rest/virtualne')
         .then(response => (this.virtMachines = response.data));
        axios
        .get('rest/getRole')
        .then(response => (this.role = response.data));
        
    },

});