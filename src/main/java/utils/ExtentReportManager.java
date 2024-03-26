package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager implements ITestListener {
    private ExtentReports extent;
    public ExtentTest test;


    public void onStart(ITestContext testContext) {
        ExtentSparkReporter sparkReporter;
        String timeStamp = new SimpleDateFormat("yyy.MM.dd.HH.mm.ss").format(new Date());
        String repName = "Test-Report-" + timeStamp + ".html";
        sparkReporter = new ExtentSparkReporter("./target/" + repName);
        sparkReporter.config().setDocumentTitle("UserApiAutomationProject");
        sparkReporter.config().setReportName("Users API");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application", "User API");
        extent.setSystemInfo("Operation System", System.getProperty("os.name"));
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("user", "irinas");
    }

    public void onTestSuccess(ITestResult result)
    {
        reportPreparation(result);
        test.log(Status.PASS, "Test Passed");
    }


    public void onTestFailure(ITestResult result)
    {
        reportPreparation(result);
        test.log(Status.FAIL, "Test Failed");
        test.log(Status.FAIL, result.getThrowable().getMessage());
    }

    public void onTestSkipped(ITestResult result){
        reportPreparation(result);
        test.log(Status.SKIP, "Test Skipped");
        test.log(Status.SKIP, result.getThrowable().getMessage());
    }

    public void onFinish(ITestContext testContext){
        extent.flush();
    }

    private void reportPreparation(ITestResult result) {
        test = extent.createTest(result.getName());
        test.createNode(result.getName());
        test.assignCategory(result.getMethod().getGroups());
    }

}
