public class AutoLogin {
	private static ChromeDriver driver;
	private static String oldUrl;
	private static String newUrl;

	static {
		// 本地测试
//		System.setProperty("webdriver.chrome.driver", "C:\\Users\\kitchen\\Desktop\\chromedriver.exe");
		// 打包
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		// 下面两行代码关闭无用的log
		System.setProperty("webdriver.chrome.silentOutput", "true");
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
		ChromeOptions chromeOptions = new ChromeOptions();
		// 这是指不打开GUI模式
		chromeOptions.addArguments("--headless");
		driver = new ChromeDriver(chromeOptions);
	}

	/**
	 * 获取登录的skey
	 * @param u
	 * @param p
	 * @return
	 */
	public static String getSkey(String u, String p) {
		String skey = null;
		if (login(u, p)) {
			skey = driver.manage().getCookieNamed("skey").toString().split(";")[0].substring(5);
		} else {
			System.out.println("本次自动登录失败，邮件通知管理员");
			String[] mail = { "QQ自动登录异常", "本次登录失败，请检测程序" };
			SendMail.send(mail);
			System.exit(0);
		}
		driver.quit();
		return skey;
	}

	/**
	 * 登录
	 * 
	 * @param u
	 * @param p
	 * @return true表示登录成功
	 */
	public static boolean login(String u, String p) {
		System.out.println("时间到,开始登录...");
		boolean flag = false;
		driver.get(
				"登录地址");
		try {
			Thread.sleep(1000);
			// 切换登录模式
			driver.findElement(By.id("switcher_plogin")).click();
			Thread.sleep(500);
			// 输入账号以及密码进行登录
			driver.findElement(By.id("u")).clear();
			driver.findElement(By.id("u")).sendKeys(u);
			driver.findElement(By.id("p")).clear();
			driver.findElement(By.id("p")).sendKeys(p);
			driver.findElement(By.id("login_button")).click();

			Thread.sleep(5000);
			int[][] arr = { { 10, 20, 30, 40, 50, 18 }, { 10, 20, 30, 40, 50, 18, 30 }, { 10, 20, 30, 40, 50, 18, 15 },

			};
			oldUrl = driver.getCurrentUrl();
			for (int i = 0; i < arr.length; i++) {
				System.out.print("正在尝试第" + (i + 1) + "次登录：");
				if (unlock(arr[i])) {
					System.out.println("登录成功");
					flag = true;
					break;
				} else {
					System.out.println("登录失败");
					flag = false;
				}
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			System.out.println("登录失败");
			driver.quit();
		}
		return flag;

	}

	/**
	 * 自动进行滑块解锁
	 * 
	 * @return true表示解锁成功
	 */
	public static boolean unlock(int[] arr) {
		boolean flag = false;
		Actions actions = new Actions(driver);
		try {
			driver.switchTo().frame("tcaptcha_iframe");
			WebElement slideBtn = driver.findElement(By.id("tcaptcha_drag_button"));
			// 按下滑动按钮
			actions.clickAndHold(slideBtn).perform();
			for (int i : arr) {
				actions.moveByOffset(i, 0).perform();
			}
			// 松开按钮
			actions.release(slideBtn).perform();
			Thread.sleep(3000);
			newUrl = driver.getCurrentUrl();
			if (oldUrl.equals(newUrl)) {
				driver.switchTo().defaultContent();
				flag = false;
			} else {
				flag = true;
			}
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("登录失败");
			driver.quit();
		}
		return flag;
	}
}
