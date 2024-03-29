$(function () {

  $("#doc_filter").contentFilter();

  $.fn.springPopover = function () {
    this.each(function (i, e) {
      var $e = $(e);
      var contents = $e.html();

      $e.html("<span class='btn'>" + $e.data('title') + "</span>").
        popover({content: contents, trigger: 'click', html: true});
    });

    return this;
  };


  //OPENS ITEM DROPDOWN WIDGET
  $(".js-item--open-dropdown").click(function () {
    var dropdownItem = $(this).parents(".js-item-dropdown--wrapper");
    var documentHeight = $(document).height();
    var headerHeight = $("header").outerHeight();
    var footerHeight = $("footer").outerHeight();
    var scrimHeight = documentHeight - headerHeight - footerHeight;

    dropdownItem.toggleClass("js-open");
    dropdownItem.siblings().removeClass("js-open");
    $(this).parents(".js-item-dropdown-widget--wrapper").siblings().find(".js-item-dropdown--wrapper").removeClass("js-open");

    $("#scrim").addClass("js-show").css("height", scrimHeight).css("top", headerHeight);
    $("#scrim").click(function () {
      $(".js-item-dropdown--wrapper").removeClass("js-open");
      $(this).removeClass("js-show");
    });
  });

  //OPENS SEARCH DROPDOWN
  $(".js-search-input-open").click(function () {
    $(".nav-search").addClass("js-highlight");
    var inputContainer = $(".js-search-dropdown");
    var input = $(".js-search-input");
    inputContainer.addClass("js-show");

    //FOCUSES SEARCH INPUT ON OPEN
    setTimeout(function () {
      input.focus();
    }, 100);

    //CLOSES SEARCH DROPDOWN
    $(".body--container, .js-search-input-close").click(function () {
      inputContainer.removeClass("js-show");
      $(".nav-search").removeClass("js-highlight");
      $("#scrim").removeClass("js-show");
    });
  });


  //AUTO OPENS SEARCH DROPDOWN ON SEARCH VIEW AND 
  if (window.location.pathname == "/search") {
    $(".nav-search").addClass("js-highlight");
    $(".js-search-dropdown").addClass("js-show no-animation");

    //PREPOPULATES INPUT WITH SEARCH QUERY AND
    var searchQuery = decodeURIComponent(window.location.search.replace(/\+/g, " "));
    var seachStart = searchQuery.search("q=");
    var searchString = searchQuery.substr(seachStart + 2);

    $(".js-search-input").val(searchString);

    //PREPOPULATES TITLE WITH SEARCH QUERY
    $(".js-search-results--title").html(searchString);

    //CLOSES SEARCH DROPDOWN
    $(".js-search-input-close").click(function () {
      $(".js-search-dropdown").removeClass("js-show no-animation");
      $(".nav-search").removeClass("js-highlight");
    });
  }
  ;

  $.fn.showPreferredLink = function () {
    this.find("li").hide();
    this.find("li." + detectOs() + detectArch()).show();
    return this;
  };
  $('.download-links').showPreferredLink();


  var moveItemSlider = function () {
    var activeItem = $(".js-item-slider--wrapper .js-item.js-active");
    if (activeItem.length == 0 ) {
      return;
    } else {
      var activeItemPosition = activeItem.position();
      var activeItemOffset = activeItemPosition.left;
      var activeItemWidth = activeItem.outerWidth();

      var slider = $(".js-item--slider");
      var sliderPosition = slider.position();
      var sliderOffset = sliderPosition.left;
      var sliderTarget = activeItemOffset - sliderOffset;

      slider.width(activeItemWidth);
      slider.css("margin-left", sliderTarget);
      
    };
  }

  moveItemSlider();

  $(".js-item").click(function () {
    $(this).addClass("js-active");
    $(this).siblings().removeClass("js-active");
    moveItemSlider();
  });

  

});


var detectOs = function () {
  if (navigator.appVersion.indexOf("Win") != -1) return "Windows";
  if (navigator.appVersion.indexOf("Mac") != -1) return "Mac";
  if (navigator.appVersion.indexOf("Linux") != -1) return "Linux";
  return "Unknown";
}

var detectArch = function () {
  if (navigator.platform.indexOf("Win64") !== -1) {
    return "64"
  }

  if (navigator.platform.indexOf("Linux x86_64") !== -1) {
    return "64";
  }

  if (/Mac OS X 10.[0-5]/.test(navigator.userAgent)) {
    return "32"
  }

  if (navigator.userAgent.indexOf("Mac OS X") !== -1) {
    return "64"
  }

  return "32";
}





