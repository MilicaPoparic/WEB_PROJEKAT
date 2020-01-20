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
	
	<table border="1">
		<tr bgcolor="blue">
			<th> Name </th>
			<th> Core </th>
			<th> RAM </th>
			<th> GPU </th>
			<th> Organization </th>
		</tr>
		
		<tr v-for="m in vms">
			<td>{{m.nameVM }}</td>
			<td>{{m.categoryCoreNumber}}</td>
			<td>{{m.categoryRAM}}</td>
			<td>{{m.categoryGPU}}</td>
			<td>{{m.nameORG}}</td>
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
			axios
		      .post('rest/viewVirt', "")
		      .then(response => location.href = '#/h');
		}
		
	},
	mounted(){
		 axios
      	.get('rest/getSearchVMS')
      	.then(response => (this.vms = response.data));

	}

});