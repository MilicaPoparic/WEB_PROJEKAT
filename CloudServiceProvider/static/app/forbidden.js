Vue.component("forbidden", {
	template: ` 
<div>
	<p><b>403 FORBIDDEN</b></p>
	<button v-on:click="back()">Back</button>
</div>		  
`,
	methods : {
		back: function(){
			location.href = '#/h';
		}
},
});