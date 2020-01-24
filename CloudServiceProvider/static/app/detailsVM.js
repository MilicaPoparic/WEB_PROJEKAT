Vue.component("change-vm",{
	data: function(){
		return{
			vm: {},
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
				<td v-if="role=='superAdmin' || role=='admin'" ><input type="text" style="width:60px" size="3" v-model="vm.nameVM" name="name" ></td> {{nameErr}}
				<td v-if="role=='user'"> {{vm.nameVM}} </td> 
		</tr>
		<tr >	
				<td> Category name: </td>
				<td>{{vm.category}}</td> {{captionErr}}
				<td v-if="role=='superAdmin'" ><a href="#/d" >Change category</a></td>
		</tr>
		<tr >	
				<td> Core Number: </td>
				<td>{{vm.categoryCoreNumber}}</td> {{captionErr}}
		</tr>
		<tr >	
				<td> RAM: </td>
				<td>{{vm.categoryRAM}}</td> {{captionErr}}
		</tr>
		<tr >	
				<td> GPU: </td>
				<td> {{vm.categoryGPU}} </td> {{captionErr}}
		</tr>
		<tr >	
				<td> Organization: </td>
				<td>{{vm.nameORG}}</td> {{captionErr}}
		</tr>
		</table>
		<table border="1">
			<th>Drives</th>
			<tr v-for="d in vm.drives">
			<td>{{d}}</td>
			</tr>
		</table>
		<p>ACTIVITY LOG</p>
		<table>
			<th>Start date</th><th>End date</th>
			<tr v-for="al in vm.activityLog">
			<td> {{al.start}}</td><td> {{al.end}}</td>
			</tr>
		</table>
		

		
		<p v-if="role=='superAdmin'">CHANGE ACTIVITY LOG </p>
		<table>
		<template v-if="role=='superAdmin'">
			<th>Start date</th><th>End date</th>
			<tr v-for="aa in vm.activityLog">
				<td><input type="date" v-model="aa.start" name="name"></td> 
				<td><input type="date" v-model="aa.end" name="noname"></td> 
			</tr>
		</template>
		</table>
		
		
		<br>
		<table>
		<td><button v-on:click="change()">Change data</button></td> 
		<td><button v-on:click="deleteVm()">Delete</button></td>
		</table>
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
		
		deleteVm : function() {
		axios
		.post('rest/deleteVM', this.vm)
		.then(response => location.href = '#/h');	
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