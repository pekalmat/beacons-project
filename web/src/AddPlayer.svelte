<script>
    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();
    
    let player = {
        name: "",
        points: 0
    };

    const onSubmit = (e) => {
        e.preventDefault();
        dispatch("addplayer", player);
        player = {
            name: "",
            points: 0
        };
    }
</script>

<form on:submit={onSubmit}>
    <input type="text" placeholder="Player Name" bind:value={player.name}>
    <input type="number" placeholder="Player Points" bind:value={player.points}>
    <input type="submit" value="Add Player">
</form>
