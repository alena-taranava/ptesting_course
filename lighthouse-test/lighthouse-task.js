const fs = require('fs')
const puppeteer = require('puppeteer')
const lighthouse = require('lighthouse/lighthouse-core/fraggle-rock/api.js')

const waitTillHTMLRendered = async (page, timeout = 30000) => {
  const checkDurationMsecs = 1000;
  const maxChecks = timeout / checkDurationMsecs;
  let lastHTMLSize = 0;
  let checkCounts = 1;
  let countStableSizeIterations = 0;
  const minStableSizeIterations = 3;

  while(checkCounts++ <= maxChecks){
    let html = await page.content();
    let currentHTMLSize = html.length; 

    let bodyHTMLSize = await page.evaluate(() => document.body.innerHTML.length);

    //console.log('last: ', lastHTMLSize, ' <> curr: ', currentHTMLSize, " body html size: ", bodyHTMLSize);

    if(lastHTMLSize != 0 && currentHTMLSize == lastHTMLSize) 
      countStableSizeIterations++;
    else 
      countStableSizeIterations = 0; //reset the counter

    if(countStableSizeIterations >= minStableSizeIterations) {
      console.log("Fully Rendered Page: " + page.url());
      break;
    }

    lastHTMLSize = currentHTMLSize;
    await page.waitForTimeout(checkDurationMsecs);
  }  
};

async function captureReport() {
	//const browser = await puppeteer.launch({args: ['--allow-no-sandbox-job', '--allow-sandbox-debugging', '--no-sandbox', '--disable-gpu', '--disable-gpu-sandbox', '--display', '--ignore-certificate-errors', '--disable-storage-reset=true']});
	const browser = await puppeteer.launch({
        "headless": false,
        args: ['--lang=en-US', '--start-maximized', '--allow-no-sandbox-job', '--allow-sandbox-debugging', '--no-sandbox', '--ignore-certificate-errors', '--disable-storage-reset=true',
        '--no-default-browser-check', '--disable-notifications', '--disable-features=Geolocation', '--disable-infobars']
    });

	const page = await browser.newPage();
	const baseURL = "http://shopizer:8080/";
	
	await page.setViewport({"width":1920,"height":1080});
	await page.setDefaultTimeout(30000);

	const navigationPromise = page.waitForNavigation({timeout: 30000, waitUntil: ['domcontentloaded']});
    // open the application
    await page.goto(baseURL);
    await navigationPromise;
		
	const flow = await lighthouse.startFlow(page, {
		name: 'shopizer',
		configContext: {
		  settingsOverrides: {
			throttling: {
			  rttMs: 40,
			  throughputKbps: 10240,
			  cpuSlowdownMultiplier: 1,
			  requestLatencyMs: 0,
			  downloadThroughputKbps: 0,
			  uploadThroughputKbps: 0
			},
			throttlingMethod: "simulate",
			screenEmulation: {
			  mobile: false,
			  width: 1920,
			  height: 1080,
			  deviceScaleFactor: 1,
			  disabled: false,
			},
			formFactor: "desktop",
			onlyCategories: ['performance'],
		  },
		},
	});

  	//================================NAVIGATE================================
    await flow.navigate(baseURL, {
		stepName: 'open the application'
		});
  	console.log('application is opened');
	
	
	//================================SELECTORS================================
    const mainMenuTables            = "div.main-menu a[href$='tables']"
    const activePageItem            = "li.page-item.active"
    // as we need to open "any" table, let's have "1" hard-coded below
    const productImg                = "div:nth-child(1) > div.product-wrap > div.product-img > a > img"
    const addToCartButton           = "div.pro-details-cart button"
    const cartCountIcon             = "button.icon-cart span"
    const cartIconButton            = "button.icon-cart"
    const viewCartButton            = "div.shopping-cart-btn a[href$=cart]"
    const proceedToCheckoutButton   = "div.cart-total-box a[href$=checkout]"
    const placeOrderButton          = "div.place-order button"

	await page.waitForSelector(mainMenuTables);
	
	//================================PAGE_ACTIONS================================
    await flow.startTimespan({ stepName: 'open Tables' });
		await page.click(mainMenuTables);
		await waitTillHTMLRendered(page);
		await page.waitForSelector(activePageItem);
	await flow.endTimespan();
	console.log('open Tables is completed');

    await flow.startTimespan({ stepName: 'click on Table' });
		await page.click(productImg);
		await waitTillHTMLRendered(page);
		await page.waitForSelector(addToCartButton);
	await flow.endTimespan();
	console.log('click on Table is completed');

    await flow.startTimespan({ stepName: 'add to Cart' });
		await page.click(addToCartButton);
		await waitTillHTMLRendered(page);
		const addedItemsCount = await page.$eval(cartCountIcon, el => el.textContent)
		await page.waitForFunction(addedItemsCount => addedItemsCount > 0, {}, addedItemsCount);
		await flow.endTimespan();
	console.log('add to Cart is completed');

    await flow.startTimespan({ stepName: 'open Cart' });
		await page.waitForSelector(cartIconButton);
		await page.click(cartIconButton);
		await page.waitForSelector(viewCartButton);
		await page.evaluate((viewCartButton) => {
	  		document.querySelector(viewCartButton).click();
		}, viewCartButton);
        await waitTillHTMLRendered(page);
        await page.waitForSelector(proceedToCheckoutButton);
	await flow.endTimespan();
	console.log('open Cart is completed');

    await flow.startTimespan({ stepName: 'proceed to checkout' });
		await page.click(proceedToCheckoutButton);
		await waitTillHTMLRendered(page);
		await page.waitForSelector(placeOrderButton);
	await flow.endTimespan();
	console.log('proceed to checkout is completed');

	//================================REPORTING================================
	const reportPath = __dirname + '/user-flow.report.html';
	//const reportPathJson = __dirname + '/user-flow.report.json';

	const report = await flow.generateReport();
	//const reportJson = JSON.stringify(flow.getFlowResult()).replace(/</g, '\\u003c').replace(/\u2028/g, '\\u2028').replace(/\u2029/g, '\\u2029');
	
	fs.writeFileSync(reportPath, report);
	//fs.writeFileSync(reportPathJson, reportJson);	

    await browser.close();
}
captureReport();