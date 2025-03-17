// let currentGroupIndex = 0;

// function searchMovies() {
//   const keyword = document.getElementById("searchKeyword").value;
//   if (!keyword.trim()) {
//     Swal.fire({
//       text: "검색어를 입력하세요",
//       icon: "warning",
//       timer: 1500,
//       showConfirmButton: false,
//     });
//     return;
//   }

//   fetch("/board/search/movies", {
//     method: "POST",
//     headers: {
//       "Content-Type": "application/x-www-form-urlencoded",
//     },
//     body: `keyword=${encodeURIComponent(keyword)}`,
//   })
//     .then((response) => response.json())
//     .then((movies) => {
//       const resultsDiv = document.getElementById("searchResult");
//       resultsDiv.innerHTML = "";

//       movies.forEach((movieGroup, index) => {
//         const groupDiv = document.createElement("div");
//         groupDiv.id = `movieGroup${index}`;
//         groupDiv.style.display = index === 0 ? "block" : "none";
//         groupDiv.className = "movie-group";

//         movieGroup.forEach((movie) => {
//           const card = `
//                 <a href="/board/movieDetail?movieId=${movie.id}">
//                     <div class="movie-card">
//                         <img src="${
//                           movie.poster_path
//                             ? "https://image.tmdb.org/t/p/w200" +
//                               movie.poster_path
//                             : "/images/blankPoster.jpg"
//                         }" 
//                             alt="${movie.title}">
//                         <h3 class="truncate">${movie.title}</h3>
//                         <p>${movie.release_date || "개봉일 미정"}</p>
//                     </div>
//                 </a>`;
//           groupDiv.innerHTML += card;
//         });

//         resultsDiv.appendChild(groupDiv);
//       });

//       currentGroupIndex = 0;
//       updateLoadMoreButton();
//     })
//     .catch((error) => {
//       console.error("Error:", error);
//     });
// }

// function loadMore() {
//   currentGroupIndex++;
//   const nextGroup = document.getElementById(`movieGroup${currentGroupIndex}`);
//   if (nextGroup) {
//     nextGroup.style.display = "flex";
//     updateLoadMoreButton();
//   }
// }

// /*function updateLoadMoreButton() {
//     const loadMoreBtn = document.getElementById('loadMoreBtn');
//     if (loadMoreBtn) {
//         loadMoreBtn.style.display = currentGroupIndex < totalGroups - 1 ? 'block' : 'none';
//     }
// }
// */
// // 페이지 로드 시 이벤트 리스너 추가
// document.addEventListener("DOMContentLoaded", () => {
//   const searchButton = document.getElementById("searchButton");
//   if (searchButton) {
//     searchButton.addEventListener("click", searchMovies);
//   }

//   const searchKeyword = document.getElementById("searchKeyword");
//   if (searchKeyword) {
//     searchKeyword.addEventListener("keypress", (e) => {
//       if (e.key === "Enter") {
//         searchMovies();
//       }
//     });
//   }

//   // 초기 "더 보기" 버튼 상태 설정
//   //updateLoadMoreButton();
// });
