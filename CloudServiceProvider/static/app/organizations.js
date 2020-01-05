Vue.component("organization", {
	data: function () {
		    return {
		      organizations: null
		    }
	},
	template: ` 
<div>
	Organizacije:
	<table border="1">
	<tr bgcolor="lightgrey">
		<th>Ime</th>
		<th>Opis</th>
		<th>Logo</th>
		<th>&nbsp;</th>
	</tr>
		
	<tr v-for="o in organizations">
		<td>{{o.name}}</td>
		<td>{{o.caption}}</td>
		<td><img src="{{o.logo}}" alt="Logo" height=5 width=5></td>
	</tr>
</table>
	
</div>		  
`,
	methods : {
		nesto : function() {
			alert("Reci mi lepa si");
		}
	}
	,
	mounted () {
        axios
          .get('rest/getOrganizations')
          .then(response => (this.organizations = response.data))
    },
});