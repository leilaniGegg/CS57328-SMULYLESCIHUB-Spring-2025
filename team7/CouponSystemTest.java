public class CouponSystemTest{
    
    CouponSystem cs;
    Rider r;

    @Before
    public void createRider(){
        r = new Rider("john_user","John","john@email.com");
    }

    @Test//this test takes care of all possibilities for subscribing and unsubscribing
    public void subscribeTest(){
        assertEquals("", cs.unsubscribe(r));
        assertEquals("john_user subscribed to coupons", cs.subscribe(r));
        assertEquals("", cs.subscribe(r));
        assertEquals("john_user unsubscribed to coupons", cs.unsubscribe(r));
    }
    
    @Test
    public void notifyObserversTest(){
        assertEquals("john_user subscribed to coupons", cs.subscribe(r));
        assertEquals("No coupon to send",cs.NotifyObservers());
        assertEquals("\nGenerated new coupon: Coupon: Summer25 (25% off) - Summer special discount\n Notification sent to John (john@email.com): Coupon: Summer25 (25% off) - Summer special discount\n", cs.sendCoupon());
    }

}