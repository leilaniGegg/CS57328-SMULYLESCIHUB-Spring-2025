public class RiderTest{
    Rider r;
    Coupoon c;

    @Before
    public void createRider(){
        r = new Rider("john_user","John","john@email.com");
    }

    @Before
    public void createCoupon(){
        c = new Coupon("ABC123", 0.5, "Description of Coupon");
    }

    @Test
    public void updateTest(){
        assertEquals("Notification sent to John (john@email.com): Coupon: ABC123 (50% off) - Description of Coupon\n", r.update(c));
    }

    @Test
    public void gettersTest(){
        assertEquals("john_user",r.getUserID());
        assertEquals("John",r.getName());
        assertEquals("john@email.com",r.getEmail());
    }

}