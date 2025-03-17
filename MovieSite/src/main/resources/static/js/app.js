
document.addEventListener("DOMContentLoaded", function() {
	const genreButtons = document.querySelectorAll('.tab-button');
	const genreContents = document.querySelectorAll('.genre-content');

	// 탭 전환 기능
	genreButtons.forEach((button, index) => {
		button.addEventListener('click', () => {
			// 모든 버튼에서 active 클래스 제거
			genreButtons.forEach(btn => btn.classList.remove('active'));
			// 클릭된 버튼에 active 클래스 추가
			button.classList.add('active');

			// 모든 콘텐츠 숨기기
			genreContents.forEach(content => content.classList.remove('active'));
			// 선택된 장르의 콘텐츠 표시
			genreContents[index].classList.add('active');
		});
	})

});

 // 대여 현황 박스를 클릭하면 모달 열기
 rentalStatusBox.addEventListener("click", function () {
	rentalModal.style.display = "block";
  });
  
// 공통 변수 설정
const movieLists = document.querySelectorAll(".movie-list");  // 모든 .movie-list 요소 선택
const arrowsLeft = document.querySelectorAll(".fa-chevron-left.arrow");  // 왼쪽 화살표
const arrowsRight = document.querySelectorAll(".fa-chevron-right.arrow");  // 오른쪽 화살표

// 화면 해상도에 따른 슬라이드 개수 설정
const getSlideCount = () => {
  const width = window.innerWidth;
  if (width >= 1920) {
    return 6;
  } else if (width >= 1680) {
    return 5;
  } else if (width >= 1440) {
    return 4;
  } else if (width >= 1280) {
    return 4;
  } else if (width >= 1024) {
    return 3;
  } else if (width >= 800) {
    return 2;
  } else {
    return 1;
  }
};

// 각 영화 리스트마다 슬라이드 기능 구현
movieLists.forEach((movieList, index) => {
  const itemWidth = 270;  // 각 아이템의 너비
  const spaceBetweenItems = 30;  // 각 아이템 간의 간격
  const totalItemWidth = itemWidth + spaceBetweenItems;  // 하나의 아이템 전체 너비
  const itemCount = movieList.querySelectorAll(".movie-list-item").length;  // 아이템 개수
  let currentIndex = 0;  // 현재 보여지는 첫 번째 아이템 인덱스

  // 슬라이드 이동 함수 (왼쪽으로)
  const slideLeft = () => {
    if (currentIndex > 0) {
      currentIndex--;
    }
    const moveAmount = currentIndex * totalItemWidth;
    movieList.style.transform = `translateX(-${moveAmount}px)`;
  };

  // 슬라이드 이동 함수 (오른쪽으로)
  const slideRight = () => {
    const slideCount = getSlideCount();  // 현재 해상도에 맞는 슬라이드 개수 가져오기

    // 남은 아이템 개수 계산
    const remainingItems = itemCount - currentIndex;

    // 남은 아이템만큼 슬라이드
    if (remainingItems <= slideCount) {
      currentIndex = itemCount - remainingItems;  // 남은 아이템만큼 끝까지 슬라이드
    } else {
      currentIndex += slideCount;  // 그 외에는 기본 슬라이드 개수만큼 이동
    }

    // 슬라이드가 넘치지 않도록 제한
    if (currentIndex > itemCount - slideCount) {
      currentIndex = itemCount - slideCount;
    }

    // 오른쪽 끝에 도달했을 때, 처음으로 돌아가기
    if (remainingItems <= slideCount) {
      currentIndex = 0;  // 오른쪽 끝에 도달하면 처음으로 돌아가기
    }

    // 슬라이드 이동
    const moveAmount = currentIndex * totalItemWidth;
    movieList.style.transform = `translateX(-${moveAmount}px)`;
  };

  // 왼쪽 화살표 클릭 이벤트
  arrowsLeft[index].addEventListener("click", slideLeft);

  // 오른쪽 화살표 클릭 이벤트
  arrowsRight[index].addEventListener("click", slideRight);
});

  // 모달 닫기 버튼 클릭하면 모달 닫기
  closeModal.addEventListener("click", function () {
	rentalModal.style.display = "none";
  });

  // 모달 밖을 클릭하면 모달 닫기
  window.addEventListener("click", function (event) {
	if (event.target === rentalModal) {
	  rentalModal.style.display = "none";
	}
  });
