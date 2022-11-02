

let myPort = browser.runtime.connect({name:"port-from-cs"});
myPort.postMessage({greeting: "hello from content script"});

myPort.onMessage.addListener((m) => {
    let msg = document.getElementById('msg');
    msg.innerText = m.phone;
});

var btnLogin = document.getElementById("btnLogin");
btnLogin.addEventListener('click', function() {
    browser.runtime.sendNativeMessage("browser", "content send to native");
    myPort.postMessage({greeting: "content send to background"});
});
