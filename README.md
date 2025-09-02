# Consumer Notification Service - Wishlist Alerts

A Spring Boot microservice that processes wishlist notifications from the QStacks queue and sends email alerts to users when their wishlisted stocks meet their specified criteria.

## 🏗️ Architecture Overview

The service follows a queue-driven architecture:

1. **QStacks Queue** → Receives wishlist IDs in format `"userId::stockId"`
2. **QStacks Consumer** → Processes queue messages
3. **Data Fetching** → Retrieves user, stock, and wishlist data from MongoDB
4. **Email Notification** → Sends formatted email to user
5. **Database Update** → Marks wishlist as notified

## 📋 Process Flow

### Input: QStacks Queue Message
```json
{
  "id": "ABC::PQR"
}
```
Where:
- `ABC` = User ID
- `PQR` = Stock ID
- `ABC::PQR` = Wishlist ID

### Processing Steps
1. **Parse Message**: Extract `userId` and `stockId` from `wishlistId`
2. **Fetch User**: Get user email from users collection
3. **Validate Email**: Ensure valid email exists, throw exception if not
4. **Fetch Stock**: Get stock details from stocks collection
5. **Fetch Wishlist**: Get notification rule from wishlists collection
6. **Send Email**: Trigger email notification to user
7. **Update Status**: Set `wishlist.notified = true` on success

### Error Handling
- **Invalid/Missing Email** → Exception + Log
- **Stock Not Found** → Exception + Log  
- **Wishlist Not Found** → Exception + Log
- **Email Send Failure** → Exception + Log

## 🗄️ Database Collections

### Users Collection
```json
{
  "_id": "uniquecode",
  "name": "Alice",
  "email": "alice@example.com",
  "phone": "+91XXXXXXXXXX",  
  "password_hash": "xxxx",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### Stocks Collection
```json
{
  "_id": "uniquecode",
  "symbol": "RELIANCE",
  "name": "Reliance Industries Ltd",
  "currentPrice": 2200,
  "exchange": "NSE",
  "sector": "Energy"
}
```

### Wishlists Collection
```json
{
  "_id": "userId::stockId",
  "userId": "user_id_ref",
  "stockId": "stock_id_ref",
  "ruleType": "percentage_increase",
  "ruleValueInPercent": "5%",
  "rateValueTargeted": 2500,
  "ruleValueAtSet": 2200,
  "createdAt": "timestamp",
  "updatedAt": "timestamp",
  "active": true,
  "notified": false
}
```

## 📧 Email Notification Format

```
Your wishlisted stock {stock name} is {wishlist_rule}. Please buy the stock quickly, before price drops or rises. Thank you for choosing Stock Pulse.
```

**Examples:**
- "Your wishlisted stock Reliance Industries Ltd is up by 5%. Please buy the stock quickly..."
- "Your wishlisted stock Reliance Industries Ltd is down by 5%. Please buy the stock quickly..."

## 🏗️ Project Structure

```
src/main/java/com/stockpulse/consumernotificationservice/
├── model/
│   ├── User.java                    # User entity
│   ├── Stock.java                   # Stock entity
│   ├── Wishlist.java               # Wishlist entity
│   └── QStacksMessage.java         # Queue message model
├── repository/
│   ├── UserRepository.java         # User data access
│   ├── StockRepository.java        # Stock data access
│   └── WishlistRepository.java     # Wishlist data access
├── service/
│   ├── WishlistNotificationProcessor.java  # Core business logic
│   └── EmailService.java           # Email sending service
├── consumer/
│   └── QStacksConsumer.java        # Queue message consumer
├── controller/
│   └── WishlistNotificationController.java # REST endpoints for testing
├── exception/
│   └── WishlistProcessingException.java    # Custom exception
└── config/
    └── ObjectMapperConfig.java     # JSON configuration
```

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.6+
- MongoDB 4.4+
- Queue System (RabbitMQ/Kafka/AWS SQS)

### Configuration

1. **MongoDB Setup**
```properties
# application.properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=stockpulse
```

2. **Queue Configuration** (Choose one)

**RabbitMQ:**
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

**Kafka:**
```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=wishlist-notification-group
```

**AWS SQS:**
```properties
cloud.aws.region.static=us-east-1
cloud.aws.credentials.access-key=your_access_key
cloud.aws.credentials.secret-key=your_secret_key
```

### Running the Service

1. **Start Dependencies**
```bash
# Start MongoDB
mongod

# Start your queue system (RabbitMQ/Kafka/etc.)
```

2. **Run the Application**
```bash
mvn spring-boot:run
```

3. **Verify Health**
```bash
curl http://localhost:8080/api/wishlist-notifications/health
```

## 🧪 Testing

### REST API Testing

1. **Test with Wishlist ID**
```bash
curl http://localhost:8080/api/wishlist-notifications/test/user123::stock456
```

2. **Test with JSON Message**
```bash
curl -X POST http://localhost:8080/api/wishlist-notifications/process-json \
  -H "Content-Type: application/json" \
  -d '{"id": "user123::stock456"}'
```

3. **Test with Message Object**
```bash
curl -X POST http://localhost:8080/api/wishlist-notifications/process \
  -H "Content-Type: application/json" \
  -d '{"id": "user123::stock456"}'
```

### Sample Test Data

**Create Test User:**
```javascript
db.users.insertOne({
  "_id": "user123",
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+91XXXXXXXXXX",
  "createdAt": new Date(),
  "updatedAt": new Date()
});
```

**Create Test Stock:**
```javascript
db.stocks.insertOne({
  "_id": "stock456",
  "symbol": "RELIANCE",
  "name": "Reliance Industries Ltd",
  "currentPrice": 2300,
  "exchange": "NSE",
  "sector": "Energy"
});
```

**Create Test Wishlist:**
```javascript
db.wishlists.insertOne({
  "_id": "user123::stock456",
  "userId": "user123",
  "stockId": "stock456",
  "ruleType": "percentage_increase",
  "ruleValueInPercent": "5%",
  "rateValueTargeted": 2310,
  "ruleValueAtSet": 2200,
  "active": true,
  "notified": false,
  "createdAt": new Date(),
  "updatedAt": new Date()
});
```

## 🔌 Queue Integration

### Enable Queue Consumer

Uncomment the appropriate consumer method in `QStacksConsumer.java`:

**For RabbitMQ:**
```java
@RabbitListener(queues = "QStacks")
public void consumeFromRabbitMQ(String message) {
    consumeQStacksMessage(message);
}
```

**For Kafka:**
```java
@KafkaListener(topics = "QStacks")
public void consumeFromKafka(String message) {
    consumeQStacksMessage(message);
}
```

**For AWS SQS:**
```java
@SqsListener("QStacks")
public void consumeFromSQS(String message) {
    consumeQStacksMessage(message);
}
```

## 📧 Email Integration

### Replace Email Simulation

Update `EmailService.java` to integrate with your email provider:

**Spring Boot Mail:**
```java
@Autowired
private JavaMailSender mailSender;

private boolean sendEmail(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);
    mailSender.send(message);
    return true;
}
```

**SendGrid:**
```java
@Value("${sendgrid.api.key}")
private String sendGridApiKey;

private boolean sendEmail(String to, String subject, String body) {
    // SendGrid implementation
}
```

## 📊 Monitoring & Logging

### Log Levels
- **INFO**: Normal processing flow
- **WARN**: Business logic warnings
- **ERROR**: Exceptions and failures

### Key Metrics to Monitor
- Queue message processing rate
- Email delivery success rate
- Exception rates by type
- Database query performance

## 🚨 Error Scenarios

| Error Type | Action | Recovery |
|------------|--------|----------|
| Invalid Email | Log + Exception | Manual review |
| User Not Found | Log + Exception | Check user data |
| Stock Not Found | Log + Exception | Check stock data |
| Wishlist Not Found | Log + Exception | Check wishlist data |
| Email Send Failure | Log + Exception + Retry | Check email service |
| Database Connection | Log + Exception + Retry | Check MongoDB |

## 🔧 Production Considerations

1. **Scaling**: Configure multiple consumer instances
2. **Monitoring**: Set up alerts for high error rates
3. **Dead Letter Queue**: Handle permanently failed messages
4. **Rate Limiting**: Prevent email service overload
5. **Database Indexing**: Index on userId, stockId, wishlistId
6. **Security**: Secure queue and database connections

## 📝 API Documentation

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/wishlist-notifications/health` | Health check |
| POST | `/api/wishlist-notifications/process` | Process message object |
| POST | `/api/wishlist-notifications/process-json` | Process JSON string |
| GET | `/api/wishlist-notifications/test/{wishlistId}` | Test with wishlist ID |

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Stock Pulse Team** 📈✨