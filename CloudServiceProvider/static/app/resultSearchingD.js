Vue.component("searchDr",{
	data: function(){
		return{
			drivess: null
		}
	},
	template:
	`
<div>
	<p>Result of searchig drives:</p>
	<table border="1" class="table">
	<tr bgcolor="#f2f2f2">
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