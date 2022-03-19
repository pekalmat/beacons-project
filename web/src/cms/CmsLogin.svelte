<script>

  import { getContext } from 'svelte';
  import Popup from '../common/Popup.svelte';
  import { createEventDispatcher } from 'svelte';

  let email;
  let password;

  const dispatch = createEventDispatcher();

  const { open } = getContext('simple-modal');

  const showPopup = (message) => {
		open(Popup, { message: message });
	};
  
  const onCmsLogin = () => dispatch("cmslogin");

  async function handleSubmit () {
    const response = await fetch('http://192.168.1.2:8081/beacons/login' , {
      method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
      body: JSON.stringify({
      email,
      password
      })
    })
    if(response.ok) {
      onCmsLogin();
    } else {
      var responseMessage = await response.text();        
      showPopup("Signup failed: " + responseMessage);
    }
  }


</script>

<form on:submit|preventDefault={handleSubmit}>
    <h1>Beacons Tracking - CMS Login</h1>
    <div class="container">
      <label for="email"><b>Email</b></label>
      <input bind:value={email} type="text" placeholder="Enter Email" name="email" required>

      <label for="psw"><b>Password</b></label>
      <input bind:value={password} type="password" placeholder=" Password" name="psw" required>
      
      <button type="submit">Login</button>

    </div>

  </form>

<style>

/* Bordered form */
form {
  border: 3px solid #f1f1f1;
}

/* Full-width inputs */
input[type=text], input[type=password] {
  width: 100%;
  padding: 12px 20px;
  margin: 8px 0;
  display: inline-block;
  border: 1px solid #ccc;
  box-sizing: border-box;
}

/* Set a style for all buttons */
button {
  background-color: #04AA6D;
  color: white;
  padding: 14px 20px;
  margin: 8px 0;
  border: none;
  cursor: pointer;
  width: 100%;
}

/* Add a hover effect for buttons */
button:hover {
  opacity: 0.8;
}

/* Extra style for the cancel button (red) */
.cancelbtn {
  width: auto;
  padding: 10px 18px;
  background-color: #f44336;
}

/* Center the avatar image inside this container */
.imgcontainer {
  text-align: center;
  margin: 24px 0 12px 0;
}

/* Avatar image */
img.avatar {
  width: 40%;
  border-radius: 50%;
}

/* Add padding to containers */
.container {
  padding: 16px;
}

/* The "Forgot password" text */
span.psw {
  float: right;
  padding-top: 16px;
}

/* Change styles for span and cancel button on extra small screens */
@media screen and (max-width: 300px) {
  span.psw {
    display: block;
    float: none;
  }
  .cancelbtn {
    width: 100%;
  }
}
</style>