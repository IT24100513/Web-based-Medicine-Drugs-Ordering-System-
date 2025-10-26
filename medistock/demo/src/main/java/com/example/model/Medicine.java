@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int quantity;

    @Column(name = "unit_price")  // IMPORTANT! Matches DB column
    private double unitPrice;

    private String category;
    private String expiry;
    private String batch;

    // Add getters and setters
}
