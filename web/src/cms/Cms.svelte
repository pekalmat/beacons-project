<script>
	import CmsLogin from './CmsLogin.svelte'
    import CmsHome from './CmsHome.svelte'
	import Modal from '../common/Modal.svelte';
	import { modal } from '../common/stores.js';
	import { createEventDispatcher } from 'svelte';

    const dispatch = createEventDispatcher();

	let userLoggedIn = false;

	const switchToHome = () => {
		userLoggedIn = true;
	}

</script>

<Modal show={$modal}>
	{#if !userLoggedIn}
		<CmsLogin on:cmslogin={switchToHome}/>
	{:else}
		<CmsHome/>
	{/if}
</Modal>
<style>

</style>