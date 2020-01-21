Vue.component("categ",{
	data: function(){
		return{
			categories:null,
			category:''
		}
	},
	template:
	`
	<div>
		
	Pregled kategorija:
	<table border="1">
	<tr bgcolor="blue">
			<th> Naziv kategorije </th>
			<th> Broj jezgara </th>
			<th> RAM </th>
			<th> GPU </th>
	</tr>
		
	<tr v-for="cat in categories">
			<td><a href="#" v-bind:class="goToDetail" @click="goToDetail(cat)">{{cat.name}}</a></td>
			<td>{{cat.coreNumber}}</td>
			<td>{{cat.RAM}}</td>
			<td>{{cat.GPUcores}}</td>
	</tr>
	
	</table>
	<br>
	<p>
	<button v-on:click="addCategory()">Dodaj kategoriju</button>
	</p>
</div>	
	`
	,
	methods:{
		addCategory:function(){
			location.href = '#/ac';
		},
		goToDetail:function(category){
			axios
		      .post('rest/categoryDetail', category)
		      .then(response => location.href = '#/d');
		}
	},
	mounted(){
		 axios
         	.get('rest/getCategories')
         	.then(response => (this.categories = response.data));
			      
	}

});