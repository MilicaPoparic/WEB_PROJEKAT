Vue.component("change-vm",{
	data: function(){
		return{
			vm: {},
			nameErr: '',
		    captionErr: '',
		    logoErr: '',
		    date1: '',
		    date2: '',
		    err: '',
		    role: ''
		}
	},
	template:
		`
<div>
		<p>VIRTUAL MACHINE DETAILS</p>
		<table>
		<tr>
				<td> Name: </td>
				<td v-if="role=='superAdmin' || role=='admin'" ><input type="text" v-model="vm.name" ></td> 
				<td><button v-if="role=='superAdmin'" v-on:click="changeActivity()">{{vm.active}}</button></td> {{nameErr}}
				<td v-if="role=='user'"> {{vm.name}} </td>
				 
		</tr>
		<tr >	
				<td> Category name: </td>
				<td>{{vm.category.name}}</td> {{captionErr}}
				<td v-if="role=='superAdmin'" ><button v-on:click="captureCategory()">Change category</button></td>
		</tr>
		<tr >	
				<td> Core Number: </td>
				<td>{{vm.category.coreNumber}}</td> {{captionErr}}
		</tr>
		<tr >	
				<td> RAM: </td>
				<td>{{vm.category.RAM}}</td> {{captionErr}}
		</tr>
		<tr >	
				<td> GPU: </td>
				<td> {{vm.category.GPUcores}} </td> {{captionErr}}
		</tr>
		<tr >	
				<td> Organization: </td>
				<td>{{vm.nameOrg}}</td> {{captionErr}}
		</tr>
		<tr><td>Drives: </td> <td> <span v-for="d in vm.drives"> {{d}}, </span> </td> </tr>

		</table>
		
		<p>ACTIVITY LOG<p>
		<table border="1">
			<tr> <th>Start date</th> <th>End date</th> </tr>
			<tr v-for="al in vm.activityLog">
				<td> {{al.start}}</td><td> {{al.end}}</td>
			</tr>
		</table>

		<p v-if="role=='superAdmin'">
		CHANGE ACTIVITY LOG 
		</p>
		<table>
		<template v-if="role=='superAdmin'">
			<th>Start date</th><th>End date</th>
			<tr v-for="aa in vm.activityLog">
				<td><input type="date" v-model="date1" name="name"></td> 
				<td><input type="date" v-model="date2" name="noname"></td> 
				<td><button v-on:click="save(aa)">save</button></td>
				<td><button v-on:click="deleteE(aa)">delete</button></td>
			</tr>
		</template>
		</table>
				
		<br>
		<table>
		<tr>{{err}}</tr>
		<td><button v-if="role!='user'" v-on:click="change()">Save changes</button></td> 
		<td><button v-if="role=='superAdmin'" v-on:click="deleteVm()">Delete</button></td>
		</table>
</div>	`
,
	methods : {
		deleteE : function(aa) {
			axios
			.post('rest/deleteActivity', aa)
			.then(response => this.vm = response.data );	
		},
		save : function(aa) {
			//treba ako je bilo koje od ova dva menjano!!!!
			if(this.date1 || this.date2){
			axios
			.post('rest/captureActivity',  {"newStart":this.date1,"start":aa.start,"newEnd":this.date2, "end":aa.end})
			.then((response) => {
	    	  if(response.status == 200) {
	    		  this.vm = response.data;
	    	  }
			})
			.catch((response)=>{
		    	  this.err='INVALID DATE!'
		      })
			this.date1=''; this.date2=''; this.err='';
			}
		},
		change : function() {
			if (this.vm.name) {
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
		
		deleteVm : function() {
		axios
		.post('rest/deleteVM', this.vm)
		.then(response => location.href = '#/h');	
		},
		changeActivity : function() {
			axios
			.post('rest/changeActivity', this.vm)
			.then(response => (this.vm = response.data));
		},
		captureCategory : function() {
			axios
			.post('rest/captureCategory', this.vm.category.name)
			.then(response => location.href = '#/d');
		}
		
		
	},
	mounted () {	
		axios
        .get('rest/testLogin')
        .then((response) => {
			    	  if(response.status == 200) {
			    		  location.href = '#/changeVM';
			    	  }
			      })
			      .catch((response)=>{
			    	  location.href = '#/';
			      })
        axios
          .get('rest/getVM')
          .then(response => (this.vm = response.data))
        axios
          .get('rest/getRole')
          .then(response => (this.role = response.data));
    },
});