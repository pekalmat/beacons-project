<script>
    import Player from '../Player.svelte';
	import AddPlayer from '../AddPlayer.svelte';



	let players = [
		{
			name: "John Doe",
			points: 53,
		},
		{
			name: "Sam Smith",
			points: 45,
		},
		{
			name: "Sara Wilson",
			points: 34,
		}
	]
	
	const addPlayer = (e) => {
		const newPlayer = e.detail;
		players = [...players, newPlayer];
	}

	const removePlayer = (e) => {
		players = players.filter(player => player.name != e.detail)
	}
</script>


<!--
	<div class="card">
		<AddPlayer on:addplayer={addPlayer}/>
		{#each players as player}
			<Player name={player.name} points={player.points} on:removeplayer={removePlayer}/>
		{/each}
	</div> -->

<style>

</style>