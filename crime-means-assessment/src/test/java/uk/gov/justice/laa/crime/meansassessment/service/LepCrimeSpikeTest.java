package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class LepCrimeSpikeTest {
    @Test
    public void executeBetweenThresholdsReturnsFull() {
        Assert.assertEquals("FULL", LepCrimeSpike.execute(new Date(), 45000));
    }

    @Test
    public void executeBelowThresholdsReturnsPass() {
        Assert.assertEquals("PASS" , LepCrimeSpike.execute(new Date(), 15000));
    }

    @Test
    public void executeAboveThresholdsReturnsFail() {
        Assert.assertEquals("FAIL" , LepCrimeSpike.execute(new Date(), 75000));
    }
}