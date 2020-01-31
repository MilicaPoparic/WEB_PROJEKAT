Vue.component("search-vm",{
	data: function(){
		return{
			vms: null
		}
	},
	template:
	`
<div>
	<p>Results of searching VMs:</p>
	
	<table border="1" class="table">
		<tr bgcolor="#f2f2f2">
			<th> Name </th>
			<th> Core </th>
			<th> RAM </th>
			<th> GPU </th>
			<th> Organization </th>
		</tr>
		
		<tr v-for="m in vms">
			<td>{{m.name}}</td>
			<td>{{m.category.coreNumber}}</td>
			<td>{{m.category.RAM}}</td>
			<td>{{m.category.GPUcores}}</td>
			<td>{{m.nameOrg}}</td>
		</tr>
	</table>
	<br>

	<br>
	<button v-on:click="back()">Back</button>	

</div>
`
  ,
  
	methods : {
		back: function(){
			location.href = '#/h';
		}
		
	},
	mounted(){
		 axios
      	.get('rest/getSearchVMS')
      	.then(response => (this.vms = response.data));

	}

});