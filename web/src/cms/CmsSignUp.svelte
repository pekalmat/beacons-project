<script>

  import { getContext } from 'svelte';
  import Popup from '../common/Popup.svelte';
  import { createEventDispatcher } from 'svelte';

  export let subscriptionPlan;
  let name;
  let email;
  let clubname;
  let password;
  let passwordConfirm;
  let errorMessage = "";

  const dispatch = createEventDispatcher();

  const { open } = getContext('simple-modal');

  const showPopup = (message) => {
		open(Popup, { message: message });
	};
  
  function checkPasswordMatches() {
    if(password != passwordConfirm) {
      errorMessage = "Passwords do not match!";
      return false;
    }
    errorMessage = "";
    return true;
  }

  async function handleSubmit () {
    if(checkPasswordMatches()) {
      const response = await fetch('http://192.168.1.9:8081/amsports/signup' , {
        method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
        body: JSON.stringify({
        name,
        email,
        clubname,
        password,
        passwordConfirm,
        subscriptionPlan
        })
      })
      if(response.ok) {
        showPopup("SignUp was Successful! Check your mail and click on the link to confirm your registration.");
      } else {
        var responseMessage = await response.text();        
        showPopup("Signup failed: " + responseMessage);
      }
    }
  }

  const handleCancel = () => dispatch("backtohome");

</script>

<form on:submit|preventDefault={handleSubmit}>
    <h1>AmSports - CMS Sign Up</h1>
    <div class="container">
        <label for="username"><b>Username</b></label>
        <input bind:value={name} type="text" placeholder="Enter Username" name="username" required>

        <label for="email"><b>Email</b></label>
        <input bind:value={email} type="text" placeholder="Enter Email" name="email" required>

        <label for="clubname"><b>Clubname</b></label>
        <input bind:value={clubname} type="text" placeholder="Enter Clubname" name="clubname" required>
    
        <label for="psw"><b>Password</b></label>
        <input bind:value={password} type="password" placeholder=" Password" name="psw" required>
        
        <label for="cpsw"><b>Confirm Password</b></label>
        <input bind:value={passwordConfirm} type="password" placeholder=" Password" name="cpsw" required>

        <p style="color: red; font-weight: bold;">{errorMessage}</p>

        <div>
            <p>Subscription Plan:</p>
            <label>
              <input type=radio bind:group={subscriptionPlan} name="plan" value={"Basic"}>
              Basic
            </label>
            <label>
              <input type=radio bind:group={subscriptionPlan} name="plan" value={"Pro"}>
              Pro
            </label>
            <label>
              <input type=radio bind:group={subscriptionPlan} name="plan" value={"Premium"}>
              Premium
            </label>
        </div>  

        <button type="submit">Sign Up</button>
    </div>
  
    <div class="container" style="background-color:#f1f1f1">
        <button  on:click={handleCancel} type="button" class="cancelbtn">Cancel</button>
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


  /* Add padding to containers */
  .container {
    padding: 16px;
  }


  /* Change styles for cancel button on extra small screens */
  @media screen and (max-width: 300px) {
    .cancelbtn {
      width: 100%;
    }
  }
</style>