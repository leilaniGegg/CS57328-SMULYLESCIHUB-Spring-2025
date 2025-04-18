public class CouponTest {
    Coupon c;
    
    //Fixture
    @Before
    public void createACoupon(){
        c = new Coupon("ABC123", 0.5, "Description of Coupon");
    }

    @Test
    public void testToString(){
        assertEquals("Coupon: ABC123 (50% off) - Description of Coupon", s.toString());
    }

    // You could seperate these, but since they're all getters, i put them together
    @Test 
    public void testGetters(){
        assertEquals("ABC123", c.getCode());
        assertEquals(0.5,getDiscount());
        assertEquals("Description of Coupon", getDescription());
    }

}