// Establish connection with app
let portFromCS;

let port = browser.runtime.connectNative("browser");
port.onMessage.addListener(response => {
    // Let's just echo the message back
    port.postMessage(`Received: ${JSON.stringify(response)}`);
    // received login result from native, send login result back to content
    portFromCS.postMessage(response);
});
port.postMessage("Init : background send to native!");


function connected(p) {
  portFromCS = p;
//  portFromCS.postMessage({greeting: "hi there content script!"});
//  portFromCS.onMessage.addListener((m) => {
//    portFromCS.postMessage({greeting: `In background script, received message from content script: ${m.greeting}`});
//  });
}

browser.runtime.onConnect.addListener(connected);

