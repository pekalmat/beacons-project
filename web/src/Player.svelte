<script>
    import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();


	export let name;
	export let points;
	let showControls = false;

	const addPoint = () => points += 1;
	const removePoint = () => points -= 1;
	const toggleControls = () => (showControls = !showControls);
    const onDelete = () => dispatch("removeplayer", name);

</script>

<div class="card">
    <h1>
        Hello {name}!
        <button on:click={toggleControls}>
            {#if showControls}-{:else}+{/if}
        </button>
        <button on:click={onDelete}>X</button>
    </h1>
    <h3>{points}</h3>
    {#if showControls}
        <button class on:click={addPoint}> +1</button>
        <button class on:click={removePoint}> -1</button>
    {/if}
</div>