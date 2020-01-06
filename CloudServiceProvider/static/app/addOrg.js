Vue.component("add-org", {
	data: function () {
	    return {
	     name: '',
	     nameErr: '',
	     caption: '',
	     captionErr: '',
	     logo: ''
	    }
},
	template: ` 
<div>
	<p>ADDING ORGANIZATION</p>
	<table>
		<tr><td>Name: </td>
		<td><input type="text" style="width:60px" size="5" v-model="name" name="name"> {{nameErr}}</td></tr>
		<tr>Caption: <input type="text" style="width:60px" size="5" v-model="caption" name="caption"> {{captionErr}}</tr>
		<tr>Logo: <input type="text" style="width:60px" size="5" v-model="logo" name="logo"> </tr>
		<tr><button v-on:click="add">Done</button></tr>
	</table>
</div>		  
`
		
	, 
	methods : {
		add : function() {
		if(!this.name){
			this.nameErr = 'Name is required!'
		}
		if(!this.caption){
			this.captionErr = 'Caption is required!'
		}
		//sad kao ako nema url da mu ja dam neki default al to cu kad budem imala ucitavanje slike 
		if(this.name && this.caption) {
			axios
			.post('rest/addOrganization', {"name":this.name, "caption":this.caption, "logo":this.logo})
			.then(response => location.href = '#/o');
			}
		}
	},
	//trebace za proveru sesije, tj da li je korisnik ulogovan

});