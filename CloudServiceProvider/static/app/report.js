Vue.component("report",{
	data: function(){
		return{
			data: {},
			date1: '',
		    date2: '',
		    err: '',
		    total:''
		}
	},
	template:
		`
<div>
		<p>MONTHLY REPORT</p>
		<table class="table">
		<tr>
				<td> Start date: </td>
				<td><input type="date" v-model="date1" name="name"></td>
		</tr>
		<tr >	
				<td> End date: </td>		
				<td><input type="date" v-model="date2" name="noname"></td> 
		</tr>
		
		<td><button v-on:click="find()">Find</button></td>
		{{err}}
		</table>
		
		<br>
		<table border="1"  class="table">
			<tr v-for="(v,k) in data"> 
				<td v-if="k!='sum'">{{k}}</td>
				<td v-if="k!='sum'">{{v}}</td>
			</tr>
		</table>
		<table border="1"  class="table">
			<tr v-for="(v,k) in data"> 
				<td v-if="k==='sum'">TOTAL</td>
				<td v-if="k==='sum'">{{v}}</td>
			</tr>
		</table>
</div>	`
,
	methods : {
		find : function() {
			if(this.date1 && this.date2){
				this.err='';
				axios
				.post('rest/findReport',  {"newStart":this.date1, "newEnd":this.date2})
				.then((response) => {
		    	  if(response.status == 200) {
		    		  this.data = response.data;
		    	  }
				})
				.catch((response)=>{
					  this.date1='';
	                  this.date2='';
			    	  this.err='INVALID DATE!'
			      })
			}
		}
	},
	mounted () {	
		axios
        .get('rest/testLogin')
        .then((response) => {
			    	  if(response.status == 200) {
			    		  axios
			  	        .get('rest/checkAdmin')
			  	        .then((response) => {
			  	        	if(response.status == 200) {
			  	        		location.href = '#/report';
			  				    }
			  				   })
			  				   .catch((response)=>{
			  				    	location.href = '#/forbidden';
			  				      })
			    	  }
			      })
			      .catch((response)=>{
			    	  location.href = '#/';
			      })
        axios
          .get('rest/getRole')
          .then(response => (this.role = response.data));
    },
});