document.addEventListener("DOMContentLoaded", () => {
  // URL에서 movieId 추출
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const movieId = urlParams.get("movieId");
  const price = document.querySelector(".rent-btn").getAttribute("data-price");

  // 모든 액션 버튼에 대한 이벤트 리스너 설정
  const buttons = document.querySelectorAll(".action-btn");

  buttons.forEach((button) => {
    button.addEventListener("click", () => {
      let action = "add"; // 기본 액션은 추가
      let endpoint = "";
      let formData = new FormData();

      if (button.classList.contains("active")) {
        action = "remove";
      }

      formData.append("movieId", movieId);
      formData.append("action", action);

      if (
        button.classList.contains("like") ||
        button.classList.contains("dislike")
      ) {
        endpoint = "/board/movieDetail/likes";
        formData.append(
          "isLike",
          button.classList.contains("like") ? "Y" : "N"
        );
      } else if (button.classList.contains("watched")) {
        endpoint = "/board/movieDetail/watched";
      } else if (button.classList.contains("bookmark")) {
        endpoint = "/board/movieDetail/bookmark";
      } else {
        return; // 알 수 없는 버튼 유형
      }

      fetch(endpoint, {
        method: "POST",
        body: formData,
      })
        .then((response) => response.json())
        .then((result) => {
          if (result.message === "good") {
            updateUI(button, result, action);
            showActionMessage(button, action);
          } else {
            alert(
              "처리 중 오류가 발생했습니다: " +
                (result.error || "알 수 없는 오류")
            );
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("요청 처리 중 오류가 발생했습니다.");
        });
    });
  });

  function updateUI(button, result, action) {
    if (
      button.classList.contains("like") ||
      button.classList.contains("dislike")
    ) {
      const likeButton = document.querySelector(".like");
      const dislikeButton = document.querySelector(".dislike");

      // 좋아요/싫어요 버튼 상태 업데이트
      if (action === "remove") {
        button.classList.remove("active");
      } else if (result.userLike === "Y") {
        likeButton.classList.add("active");
        dislikeButton.classList.remove("active");
      } else if (result.userLike === "N") {
        dislikeButton.classList.add("active");
        likeButton.classList.remove("active");
      } else {
        // 중립 상태 (둘 다 선택되지 않음)
        likeButton.classList.remove("active");
        dislikeButton.classList.remove("active");
      }

      // 좋아요/싫어요 카운트 업데이트
      updateCount(likeButton, result.likeCount);
      updateCount(dislikeButton, result.dislikeCount);
    } else {
      // 봤어요와 북마크 버튼 처리
      if (action === "remove") {
        button.classList.remove("active");
      } else {
        button.classList.toggle("active");
      }

      //	        button.classList.toggle('active', action === "add");
      updateCount(button, result.count);
    }
  }

  function updateCount(button, count) {
    const countElement = button.querySelector(".count");
    if (countElement && count !== undefined) {
      countElement.textContent = count;
    }
  }

  function showActionMessage(button, action) {
    let message = "";
    if (button.classList.contains("like")) {
      message =
        action === "add"
          ? "좋아요가 저장되었습니다!"
          : "좋아요가 취소되었습니다!";
    } else if (button.classList.contains("dislike")) {
      message =
        action === "add"
          ? "싫어요가 저장되었습니다!"
          : "싫어요가 취소되었습니다!";
    } else if (button.classList.contains("watched")) {
      message =
        action === "add"
          ? "시청 완료로 표시되었습니다!"
          : "시청 완료 표시가 취소되었습니다!";
    } else if (button.classList.contains("bookmark")) {
      message =
        action === "add"
          ? "북마크에 추가되었습니다!"
          : "북마크에서 제거되었습니다!";
    }
    Swal.fire({
      text: message,
      icon: "success",
      timer: 1500,
      showConfirmButton: false,
    });
  }

  //대여 관련
  const rentBtn = document.querySelector(".rent-btn");
  rentBtn.addEventListener("click", async () => {
    try {
      // 마일리지 정보 조회
      const response = await fetch("/board/movieDetail/getMileage");
      const mileageData = await response.json();
      if (!response.ok) {
        throw new Error("마일리지 정보를 불러오는데 실패했습니다.");
      }

      const result = await Swal.fire({
        title: "영화 대여",
        html: `
          결제 방식을 선택해주세요<br>
          <small style="color: #666;">보유 마일리지: <span id="mileage">${mileageData.mileage}</span>P</small>
        `,
        icon: "question",
        showCancelButton: true,
        showDenyButton: true,
        confirmButtonText: "현금결제",
        denyButtonText: "마일리지",
        cancelButtonText: "취소",
        reverseButtons: false
      });

      if (!result.isConfirmed && !result.isDenied) return;

      const paymentMethod = result.isConfirmed ? "cash" : "mileage";
      
      // 마일리지 결제 시 잔액 검증
      if (paymentMethod === "mileage") {
        if (!mileageData.mileage || !price) {
          throw new Error("결제 정보가 올바르지 않습니다.");
        }
        if (parseInt(mileageData.mileage) < parseInt(price)) {
          await Swal.fire({
            title: "마일리지 부족",
            text: `마일리지가 부족합니다. (필요: ${price}P, 보유: ${mileageData.mileage}P)`,
            icon: "error"
          });
          return;
        }
      }

      // 결제 진행
      const loadingAlert = Swal.fire({
        title: "결제 진행 중",
        text: "잠시만 기다려주세요...",
        allowOutsideClick: false,
        didOpen: () => Swal.showLoading()
      });

      try {
        const formData = new FormData();
        formData.append("movieId", movieId);
        formData.append("paymentMethod", paymentMethod);

        // 타임아웃 설정
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 10000); // 10초 타임아웃

        const rentResponse = await fetch("/board/movieDetail/rent", {
          method: "POST",
          body: formData,
          signal: controller.signal
        });

        clearTimeout(timeoutId);

        if (!rentResponse.ok) {
          throw new Error('서버 응답 오류: ' + rentResponse.status);
        }

        const rentData = await rentResponse.json();
        await loadingAlert.close();
        
        if (rentData.message === "good") {
          const successMessage = paymentMethod === "mileage"
            ? `영화가 성공적으로 대여되었습니다!<br><br>· 사용한 포인트: ${price}P<br>· 남은 포인트: ${mileageData.mileage - price}P`
            : "영화가 성공적으로 대여되었습니다!";
            
          await Swal.fire({
            title: "결제 완료",
            html: successMessage,
            icon: "success"
          });
          
          location.reload();
        } else {
          throw new Error(rentData.error || "결제 처리 중 오류가 발생했습니다.");
        }
        
      } catch (error) {
        await loadingAlert.close();
        await Swal.fire({
          title: "결제 오류",
          text: error.message || "결제 처리 중 오류가 발생했습니다.",
          icon: "error"
        });
      }
      
    } catch (error) {
      console.error("결제 오류:", error);
      await Swal.fire({
        title: "결제 오류",
        text: error.message || "결제 처리 중 오류가 발생했습니다.",
        icon: "error"
      });
    }
  });

  //리뷰 관련
  const reviewText = document.getElementById("reviewText");
  const submitReview = document.getElementById("submitReview");
  const reviewList = document.querySelector(".review-show-container");
  const showReview = document.getElementById("showReview");

  showReview.addEventListener("click", function () {
    if (!reviewList.classList.contains("show")) {
      Swal.fire({
        title: "리뷰 보기",
        html: '리뷰를 보시겠습니까? <span style="color: red; font-weight: bold;">(스포주의!)</span>',
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "확인",
        cancelButtonText: "취소",
        reverseButtons: false,
      }).then((result) => {
        if (result.isConfirmed) {
          reviewList.classList.add("show");
        }
      });
    } else {
      reviewList.classList.remove("show");
    }
  });

  submitReview.addEventListener("click", () => {
    const review = reviewText.value.trim();
    const action = "add";

    if (review === "") {
      Swal.fire({
        text: "리뷰를 작성해주세요.",
        icon: "question",
      });
      return;
    }

    const newForm = new FormData();
    newForm.append("movieId", movieId);
    newForm.append("content", review);

    fetch("/board/movieDetail/reviews", {
      method: "POST",
      body: newForm,
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.action) {
          Swal.fire({
            title: data.action === "save" ? "등록 완료!" : "수정 완료!",
            text:
              data.action === "save"
                ? "리뷰가 등록되었습니다."
                : "리뷰가 수정되었습니다.",
            icon: "success",
          }).then((result) => {
            if (result.isConfirmed) {
              location.reload();
            }
          });
        } else {
          throw new Error("리뷰 등록에 실패했습니다.");
        }
      })
      .catch((error) => {
        Swal.fire({
          title: "오류",
          text: error.message,
          icon: "error",
        });
      });

    // 입력 필드 초기화
    reviewText.value = "";
  });

  // HTML 이스케이프 함수
  function escapeHTML(str) {
    return str.replace(
      /[&<>'"]/g,
      (tag) =>
        ({
          "&": "&amp;",
          "<": "&lt;",
          ">": "&gt;",
          "'": "&#39;",
          '"': "&quot;",
        }[tag] || tag)
    );
  }
});

//백드롭-포스터 탭전환
const backdropButton = document.getElementById("backdropButton");
const posterButton = document.getElementById("posterButton");
const backdropSection = document.getElementById("backdropSection");
const posterSection = document.getElementById("posterSection");

backdropButton.addEventListener("click", function () {
  backdropButton.classList.add("active");
  posterButton.classList.remove("active");
  backdropSection.style.display = "block";
  posterSection.style.display = "none";
});

posterButton.addEventListener("click", function () {
  posterButton.classList.add("active");
  backdropButton.classList.remove("active");
  posterSection.style.display = "block";
  backdropSection.style.display = "none";
});
