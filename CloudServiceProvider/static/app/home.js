Vue.component("home-page", {
	data: function(){
		return{
			virtMachines: null,
			pretraga: "",
			role: ""
		}
	},
	template: ` 
<div>
		
	<p>Virtual machines</p>
	<table border="1">
		<tr bgcolor="blue">
			<th> Name </th>
			<th> Core </th>
			<th> RAM </th>
			<th> GPU </th>
			<th> Organization </th>
		</tr>
		
		<tr v-for="m in virtMachines">
			<td>{{m.nameVM }}</td>
			<td>{{m.categoryCoreNumber}}</td>
			<td>{{m.categoryRAM}}</td>
			<td>{{m.categoryGPU}}</td>
			<td>{{m.nameORG}}</td>
		</tr>
	</table>
	<br>
	<span>
		<input type="text" style="width:60px" size="5" v-model="pretraga" name="search">
		<button v-on:click="research()">Pretrazi</button>
	</span>
	 <p>
		<button v-on:click="filter()">Filter</button>
	</p>
	 
	<button v-on:click="dodajVM()">Add new vm</button>
	<a href="#/profile">Profile</a>
	<a href="#/drives" @click="drivess()">Drives</a>
	<a href="#/o" v-if="role=='superAdmin' || role=='admin'">Organizations</a>
	<a href="#/users" v-if="role=='superAdmin' || role=='admin'">Users</a>
	<a href="#/c" v-if="role=='superAdmin'">Categories</a>
	<button v-on:click="logout">Logout</button>
	
	
</div>		  
`
	, 
	methods : {
		research : function() {
			alert("Treba da implementiramo pocetnu stranicu!");
		},
		filter : function() {
			alert("Treba da implementiramo pocetnu stranicu!");
		},
		dodajVM : function() {
			alert("Treba da implementiramo pocetnu stranicu!");
		},
		logout : function() {
			axios
		      .post('rest/logout', "nesto")
		      .then(response => location.href = '#/');

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