Vue.component("change-vm",{
	data: function(){
		return{
			vm: null,
			nameErr: '',
		    captionErr: '',
		    logoErr: '',
		    role: ''
		}
	},
	template:
		`
<div>
		<p>Virtual machines:</p>
		<table>
		<tr>
				<td> Name: </td>
				<td><input type="text" style="width:60px" size="3" v-model="vm.nameVM" name="name" ></td> {{nameErr}}
		</tr>
			
		<tr >	
				<td> Core Number: </td>
				<td>{{vm.categoryCoreNumber}}</td> {{captionErr}}
		</tr>
		<tr >	
				<td> Category name: </td>
				<td>{{vm.category}}</td> {{captionErr}}
		</tr>
		<tr >	
				<td> RAM: </td>
				<td>{{vm.categoryRAM}}></td> {{captionErr}}
		</tr>
		<tr >	
				<td> GPU: </td>
				<td> {{vm.categoryGPU}} ></td> {{captionErr}}
		</tr>
		<tr >	
				<td> Organization: </td>
				<td>{{vm.nameORG}}></td> {{captionErr}}
		</tr>
		</table>
		<table border="1">
			<th>Drives</th>
			<tr v-for="d in vm.drives">
			<td>{{d}}</td>
			</tr>
		</table>
		<a href="#" v-on:click="ActivityLog(m)">Activity log</a>
		<br>
		<button v-on:click="change()">Change data</button> 
		<button v-on:click="deleteOrg()">Delete</button>
</div>	`
,
	methods : {
		change : function() {
			if (this.vm.nameVM) {
				axios
				.post('rest/changeVM', this.vm)
				.then((response) => {
		    	  if(response.status == 200) {
		    		  location.href = '#/h';
		    	  }
		      })
				.catch(response=>this.nameErr = 'Name must me unique!')
			}
		},
		//ovo cemo izmeniti kad budemo trebale!!!
		/*deleteOrg : function() {
		axios
		.post('rest/deleteOrg', this.org)
		.then(response => location.href = '#/o');	
		},
		onUpload(event) {
			this.org.logo = (event.target.files)[0].name;
		}*/
	},
	mounted () {	
        axios
          .get('rest/getVM')
          .then(response => (this.vm = response.data))
        axios
          .get('rest/getRole')
          .then(response => (this.role = response.data));
    },
});