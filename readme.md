** Project structure

memory-game/
│
├── src/
│   ├── controller/
│   │   ├── GameController.java
│   │   ├── AuthController.java
│   │   ├── FriendController.java
│   │   ├── MatchmakingController.java
│   │   ├── LeaderboardController.java
│   │
│   ├── model/
│   │   ├── Player.java
│   │   ├── Match.java
│   │   ├── GameState.java
│   │   ├── Leaderboard.java
│   │   ├── FriendRequest.java
│   │   ├── Cup.java
│   │
│   ├── view/
│   │   ├── LoginView.java
│   │   ├── GameView.java
│   │   ├── LeaderboardView.java
│   │   ├── LobbyView.java
│   │   ├── MatchView.java
│   │
│   ├── network/
│   │   ├── SocketManager.java
│   │   ├── ClientHandler.java
│   │   ├── GameServer.java
│   │
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── GameService.java
│   │   ├── MatchmakingService.java
│   │   ├── LeaderboardService.java
│   │   ├── FriendService.java
│   │
│   ├── main/
│   │   ├── ServerMain.java
│   │   ├── ClientMain.java
│   │
└── resources/


** Project description:

Hệ thống: Trò chơi bao gồm một server và nhiều client kết nối với nhau. Server sẽ quản lý các trận đấu, lưu trữ thông tin người chơi, xử lý bảng xếp hạng và cúp.

Đăng nhập: Người chơi cần đăng nhập vào tài khoản của mình từ một máy client. Sau khi đăng nhập thành công, giao diện sẽ hiển thị danh sách các người chơi đang trực tuyến cùng với các thông tin: tên người chơi, tổng số điểm hiện có, số lượng cúp đã giành được, và trạng thái (đang chơi, đang online hoặc đang offline).

Kết bạn: Người chơi có thể tìm kiếm và gửi yêu cầu kết bạn đến các người chơi khác. Khi yêu cầu được chấp nhận, người chơi có thể dễ dàng mời bạn bè tham gia vào các trận đấu.

Chế độ chơi:

    1v1 (Trận đấu xếp hạng): Đây là chế độ thi đấu xếp hạng giữa hai người chơi:
        Người chơi có thể gửi lời mời tham gia trận đấu xếp hạng cho một người chơi khác từ danh sách trực tuyến.
        Khi người chơi khác chấp nhận, cả hai sẽ được đưa vào một phòng chơi riêng, và trận đấu sẽ bắt đầu.
        Điểm thưởng: Nếu thắng, người chơi sẽ nhận được 10 cúp, nếu thua sẽ bị trừ 5 cúp. Các cúp này sẽ ảnh hưởng đến thứ hạng của người chơi trên bảng xếp hạng.
        Luật chơi: Trận đấu diễn ra theo luật của trò chơi kiểm tra trí nhớ (chi tiết luật chơi bên dưới).
    Chế độ nhiều người chơi (Trận đấu giao hữu): Đây là chế độ thi đấu giữa nhiều người chơi:
        Một người chơi có thể tạo phòng chơi giao hữu và mời bạn bè tham gia, hoặc để người chơi khác tự do tham gia vào phòng.
        Không giới hạn số lượng người chơi trong một phòng, tất cả người chơi sẽ thi đấu cùng nhau.
        Không xếp hạng: Trận đấu giao hữu không ảnh hưởng đến số lượng cúp của người chơi, không có cúp nào được trao hay bị mất. Đây là chế độ giúp người chơi giải trí và luyện tập mà không cần lo lắng về thứ hạng.
        Luật chơi: Trận đấu cũng diễn ra theo luật của trò chơi kiểm tra trí nhớ (chi tiết luật chơi bên dưới).

Luật chơi:

    Tạo bộ nhớ: Server sẽ tạo ra một danh sách các cặp hình ảnh hoặc từ ngẫu nhiên và hiển thị chúng trong một khoảng thời gian nhất định (ví dụ: 10 giây).
    Giai đoạn nhớ: Sau khi thời gian hiển thị kết thúc, các cặp hình ảnh hoặc từ sẽ bị ẩn đi. Người chơi sẽ phải nhớ và chọn các cặp hình ảnh hoặc từ mà họ cho là đúng.
    Lượt chơi:
        Mỗi lượt, người chơi sẽ lần lượt chọn một cặp mà họ nhớ. Server sẽ kiểm tra xem cặp đó có đúng không.
        Nếu cặp đó đúng, người chơi sẽ được cộng một điểm và cặp hình ảnh hoặc từ đó sẽ được loại bỏ khỏi danh sách. Nếu chọn sai, lượt chơi sẽ chuyển sang người chơi tiếp theo.
        Trận đấu sẽ tiếp tục cho đến khi tất cả các cặp hình ảnh hoặc từ đã được chọn đúng hoặc thời gian quy định kết thúc.
    Kết thúc trận đấu:
        Trong chế độ 1v1: Trận đấu kết thúc khi tất cả các cặp đã được tìm ra hoặc hết thời gian chơi. Người chơi có số điểm cao hơn sẽ thắng trận và nhận 10 cúp, người thua sẽ bị trừ 5 cúp. Nếu hòa, không ai nhận được cúp hoặc bị trừ cúp.
        Trong chế độ nhiều người chơi: Trận đấu kết thúc khi tất cả các cặp đã được tìm ra hoặc hết thời gian chơi. Người chơi có số điểm cao nhất sẽ được công nhận là người chiến thắng, nhưng không nhận được cúp.

Giao diện:

    Giai đoạn hiển thị: Giao diện trò chơi sẽ hiển thị toàn bộ các cặp hình ảnh hoặc từ trong một khoảng thời gian nhất định để người chơi ghi nhớ.
    Giai đoạn đoán: Sau khi danh sách bị ẩn đi, giao diện sẽ hiển thị các ô chứa hình ảnh hoặc từ đã bị ẩn, người chơi sẽ nhấp vào các ô để chọn cặp mà họ nhớ.
    Thông báo kết quả: Sau mỗi lần chọn, kết quả của người chơi sẽ được hiển thị ngay lập tức, cho biết cặp đó đúng hay sai, và số điểm hiện tại của mỗi người chơi sẽ được cập nhật.

Thoát khỏi trận đấu: Người chơi có thể thoát khỏi trận đấu bất cứ lúc nào. Tuy nhiên, trong chế độ 1v1, nếu thoát trước khi trận đấu kết thúc, người chơi sẽ bị tính là thua và bị trừ 5 cúp. Trong chế độ nhiều người chơi, việc thoát trận đấu không ảnh hưởng đến cúp hoặc điểm số.

Xếp hạng và cúp:

    Bảng xếp hạng: Kết quả của các trận đấu xếp hạng sẽ được lưu trữ trên server. Mỗi người chơi có thể xem bảng xếp hạng toàn hệ thống, được sắp xếp theo tổng số cúp mà mỗi người chơi đã giành được.
    Cúp: Cúp sẽ được trao cho người chiến thắng trong mỗi trận đấu xếp hạng (1v1). Người chơi càng có nhiều cúp, thứ hạng của họ càng cao.
    Hệ thống cúp: Server có thể cung cấp nhiều loại cúp khác nhau tùy vào độ khó của trận đấu hoặc các sự kiện đặc biệt trong game.