Vue.component("searchDr",{
	data: function(){
		return{
			drivess: null
		}
	},
	template:
	`
<div>
	View drives:
	<table border="1">
	<tr bgcolor="gray">
			<th> Name </th>
			<th> Capacity </th>
			<th> VM </th>
	</tr>
		
	<tr v-for="d in drivess">
			<td>{{d.name}}</a></td>
			<td>{{d.capacity}}</td>
			<td>{{d.nameVM}}</td>
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
		      .post('rest/viewDrives', "")
		      .then(response => location.href = '#/drives');
		}
		
	},
	mounted(){
		 axios
      	.get('rest/getSearchDrives')
      	.then(response => (this.drivess = response.data));

	}

});