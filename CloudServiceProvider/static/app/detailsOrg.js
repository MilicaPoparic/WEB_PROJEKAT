Vue.component("change-org",{
	data: function(){
		return{
			org: null,
			nameErr: '',
		    captionErr: '',
		    logoErr: ''
		}
	},
	template:
		`
<div>
		<p>Organizacije:</p>
		<table>
		<tr>
				<td> Name: </td>
				<td><input type="text" style="width:60px" size="3" v-model="org.name" name="name" ></td> {{nameErr}}
		</tr>
			
		<tr >	
				<td> Caption: </td>
				<td><input type="text" style="width:60px" size="3" v-model="org.caption" name="caption"></td> {{captionErr}}
		</tr>
			
		<tr>
				<td> Log: </td>
				<td><input type="text" style="width:60px" size="3" v-model="org.logo" name="logo"></td> {{logoErr}}

		</tr>
		<tr>
			<td><button v-on:click="change()">Change data</button></td> 
			<td><button v-on:click="deleteOrg()">Delete</button></td> 
		</tr>
		</table>
		

</div>
			`
,
	methods : {
		change : function() {
			if(!this.org.name){
				this.nameErr = 'Name is required!'
			}
			if(!this.org.caption){
				this.captionErr = 'Caption is required!'
			}
			if (!this.org.logo){
				this.logoErr = 'Logo is required!'
			}
			if (this.org.name && this.org.caption && this.org.logo) {
				axios
				.post('rest/changeOrg', this.org)
				.then(response => location.href = '#/o');	
			}
		},
		deleteOrg : function() {
		axios
		.post('rest/deleteOrg', this.org)
		.then(response => location.href = '#/o');	
	}
	},
	mounted () {	
        axios
          .get('rest/getOrganization')
          .then(response => (this.org = response.data))
    },
});