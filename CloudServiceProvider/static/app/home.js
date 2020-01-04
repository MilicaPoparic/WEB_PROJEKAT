Vue.component("home-page", {
	data: function(){
		return{
			virtMachines: null,
			pretraga: ""
		}
	},
	template: ` 
<div>
		
	<p>Pregled virtualnih mašina:</p>
	<table border="1">
		<tr bgcolor="blue">
			<th> Naziv </th>
			<th> Broj jezgara </th>
			<th> RAM </th>
			<th> GPU </th>
			<th> Organizacija </th>
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
		<button v-on:click="filter()">Filtriraj</button>
	</p>
	 
		<button v-on:click="dodajVM()">Dodaj novu VM</button>
	<a href="#/o">Organizations</a>
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
			    		  //ako je status 200 treba usput i da dobavi podatke koje ce da prikaze za vm 
			    	  }
			      })
			      .catch((response)=>{
			    	  location.href = '#/';
			      })
        axios
        .get('rest/virtualne')
        .then(response => (this.virtMachines = response.data));
      
    },

});