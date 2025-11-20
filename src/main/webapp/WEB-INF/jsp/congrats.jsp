<!DOCTYPE html>
<html lang="en">
	<!--begin::Head-->
	<head>
		<title>SIB</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<meta property="og:locale" content="en_US" />
		<meta property="og:type" content="article" />
		<meta property="og:title" content="SIB" />
		<meta property="og:site_name" content="SIB" />
		<!--begin::Global Stylesheets Bundle(mandatory for all pages)-->
		<link href="assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<!--end::Global Stylesheets Bundle-->




		<script>



			'use strict';

window.onload = function() {
  // Globals
  var random = Math.random
    , cos = Math.cos
    , sin = Math.sin
    , PI = Math.PI
    , PI2 = PI * 2
    , timer = undefined
    , frame = undefined
    , confetti = [];

  var particles = 10
    , spread = 40
    , sizeMin = 3
    , sizeMax = 12 - sizeMin
    , eccentricity = 10
    , deviation = 100
    , dxThetaMin = -.1
    , dxThetaMax = -dxThetaMin - dxThetaMin
    , dyMin = .13
    , dyMax = .18
    , dThetaMin = .4
    , dThetaMax = .7 - dThetaMin;

  var colorThemes = [
    function() {
      return color(200 * random()|0, 200 * random()|0, 200 * random()|0);
    }, function() {
      var black = 200 * random()|0; return color(200, black, black);
    }, function() {
      var black = 200 * random()|0; return color(black, 200, black);
    }, function() {
      var black = 200 * random()|0; return color(black, black, 200);
    }, function() {
      return color(200, 100, 200 * random()|0);
    }, function() {
      return color(200 * random()|0, 200, 200);
    }, function() {
      var black = 256 * random()|0; return color(black, black, black);
    }, function() {
      return colorThemes[random() < .5 ? 1 : 2]();
    }, function() {
      return colorThemes[random() < .5 ? 3 : 5]();
    }, function() {
      return colorThemes[random() < .5 ? 2 : 4]();
    }
  ];
  function color(r, g, b) {
    return 'rgb(' + r + ',' + g + ',' + b + ')';
  }

  // Cosine interpolation
  function interpolation(a, b, t) {
    return (1-cos(PI*t))/2 * (b-a) + a;
  }

  // Create a 1D Maximal Poisson Disc over [0, 1]
  var radius = 1/eccentricity, radius2 = radius+radius;
  function createPoisson() {
    // domain is the set of points which are still available to pick from
    // D = union{ [d_i, d_i+1] | i is even }
    var domain = [radius, 1-radius], measure = 1-radius2, spline = [0, 1];
    while (measure) {
      var dart = measure * random(), i, l, interval, a, b, c, d;

      // Find where dart lies
      for (i = 0, l = domain.length, measure = 0; i < l; i += 2) {
        a = domain[i], b = domain[i+1], interval = b-a;
        if (dart < measure+interval) {
          spline.push(dart += a-measure);
          break;
        }
        measure += interval;
      }
      c = dart-radius, d = dart+radius;

      // Update the domain
      for (i = domain.length-1; i > 0; i -= 2) {
        l = i-1, a = domain[l], b = domain[i];
        // c---d          c---d  Do nothing
        //   c-----d  c-----d    Move interior
        //   c--------------d    Delete interval
        //         c--d          Split interval
        //       a------b
        if (a >= c && a < d)
          if (b > d) domain[l] = d; // Move interior (Left case)
          else domain.splice(l, 2); // Delete interval
        else if (a < c && b > c)
          if (b <= d) domain[i] = c; // Move interior (Right case)
          else domain.splice(i, 0, c, d); // Split interval
      }

      // Re-measure the domain
      for (i = 0, l = domain.length, measure = 0; i < l; i += 2)
        measure += domain[i+1]-domain[i];
    }

    return spline.sort();
  }

  // Create the overarching container
  var container = document.createElement('div');
  container.style.position = 'fixed';
  container.style.top      = '0';
  container.style.left     = '0';
  container.style.width    = '100%';
  container.style.height   = '0';
  container.style.overflow = 'visible';
  container.style.zIndex   = '9999';

  // Confetto constructor
  function Confetto(theme) {
    this.frame = 0;
    this.outer = document.createElement('div');
    this.inner = document.createElement('div');
    this.outer.appendChild(this.inner);

    var outerStyle = this.outer.style, innerStyle = this.inner.style;
    outerStyle.position = 'absolute';
    outerStyle.width  = (sizeMin + sizeMax * random()) + 'px';
    outerStyle.height = (sizeMin + sizeMax * random()) + 'px';
    innerStyle.width  = '100%';
    innerStyle.height = '100%';
    innerStyle.backgroundColor = theme();

    outerStyle.perspective = '50px';
    outerStyle.transform = 'rotate(' + (360 * random()) + 'deg)';
    this.axis = 'rotate3D(' +
      cos(360 * random()) + ',' +
      cos(360 * random()) + ',0,';
    this.theta = 360 * random();
    this.dTheta = dThetaMin + dThetaMax * random();
    innerStyle.transform = this.axis + this.theta + 'deg)';

    this.x = window.innerWidth * random();
    this.y = -deviation;
    this.dx = sin(dxThetaMin + dxThetaMax * random());
    this.dy = dyMin + dyMax * random();
    outerStyle.left = this.x + 'px';
    outerStyle.top  = this.y + 'px';

    // Create the periodic spline
    this.splineX = createPoisson();
    this.splineY = [];
    for (var i = 1, l = this.splineX.length-1; i < l; ++i)
      this.splineY[i] = deviation * random();
    this.splineY[0] = this.splineY[l] = deviation * random();

    this.update = function(height, delta) {
      this.frame += delta;
      this.x += this.dx * delta;
      this.y += this.dy * delta;
      this.theta += this.dTheta * delta;

      // Compute spline and convert to polar
      var phi = this.frame % 7777 / 7777, i = 0, j = 1;
      while (phi >= this.splineX[j]) i = j++;
      var rho = interpolation(
        this.splineY[i],
        this.splineY[j],
        (phi-this.splineX[i]) / (this.splineX[j]-this.splineX[i])
      );
      phi *= PI2;

      outerStyle.left = this.x + rho * cos(phi) + 'px';
      outerStyle.top  = this.y + rho * sin(phi) + 'px';
      innerStyle.transform = this.axis + this.theta + 'deg)';
      return this.y > height+deviation;
    };
  }

  function poof() {
    if (!frame) {
      // Append the container
      document.body.appendChild(container);

      // Add confetti
      var theme = colorThemes[0]
        , count = 0;
      (function addConfetto() {
        var confetto = new Confetto(theme);
        confetti.push(confetto);
        container.appendChild(confetto.outer);
        timer = setTimeout(addConfetto, spread * random());
      })(0);

      // Start the loop
      var prev = undefined;
      requestAnimationFrame(function loop(timestamp) {
        var delta = prev ? timestamp - prev : 0;
        prev = timestamp;
        var height = window.innerHeight;

        for (var i = confetti.length-1; i >= 0; --i) {
          if (confetti[i].update(height, delta)) {
            container.removeChild(confetti[i].outer);
            confetti.splice(i, 1);
          }
        }

        if (timer || confetti.length)
          return frame = requestAnimationFrame(loop);

        // Cleanup
        document.body.removeChild(container);
        frame = undefined;
      });
    }
  }

  poof();
};


			
		</script>

		<style>
			





		</style>
	</head>
	<!--end::Head-->
	<!--begin::Body-->
	<body id="kt_body" data-bs-spy="scroll" data-bs-target="#kt_landing_menu" class="bg-body position-relative" style="background: white;">
    <!--begin::Theme mode setup on page load-->
    <script>var defaultThemeMode = "light"; var themeMode; if ( document.documentElement ) { if ( document.documentElement.hasAttribute("data-bs-theme-mode")) { themeMode = document.documentElement.getAttribute("data-bs-theme-mode"); } else { if ( localStorage.getItem("data-bs-theme") !== null ) { themeMode = localStorage.getItem("data-bs-theme"); } else { themeMode = defaultThemeMode; } } if (themeMode === "system") { themeMode = window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light"; } document.documentElement.setAttribute("data-bs-theme", themeMode); }</script>
    <!--end::Theme mode setup on page load-->
    <!--begin::Main-->
    <!--begin::Root-->
    <div class="d-flex flex-column flex-root">
      <!--begin::Header Section-->
      <div class="mb-0" id="home">
        <!--begin::Wrapper-->
  
        <!--end::Wrapper-->
        <!--begin::Curve bottom-->
  
        <!--end::Curve bottom-->
      </div>
      <!--end::Header Section-->
      <!--begin::How It Works Section-->
  
      <!--end::How It Works Section-->
      <!--begin::Statistics Section-->
  
      <!--end::Statistics Section-->
      <!--begin::Team Section-->
  
      <!--end::Team Section-->
      <!--begin::Projects Section-->
  
      <!--end::Projects Section-->
      <!--begin::Pricing Section-->
  
      <!--end::Pricing Section-->
      <!--begin::Testimonials Section-->
      <div class="mt-10 mb-n20 position-relative z-index-2">
        <!--begin::Container-->
        <div class="container">
          <!--begin::Heading-->
  
          <!--end::Heading-->
          <!--begin::Row-->
  
          <!--end::Row-->
          <!--begin::Highlight-->
          <div class="d-flex flex-stack flex-wrap flex-md-nowrap card-rounded shadow p-8 p-lg-12 mb-n5 mb-lg-n13" style="background: linear-gradient(90deg, #49B961 0%, #129F5D 100%);">
            <!--begin::Content-->
            <div class="my-2 me-5">
              <!--begin::Title-->
              <div class="fs-1 fs-lg-2qx fw-bold text-white mb-2">Let the journey to <span class="fw-normal">many more Dreams Begin</span></div>
              <!--end::Title-->
              <!--begin::Description-->
              <div class="fs-6 fs-lg-5 text-white fw-semibold opacity-75">First car loan from your branch has been successfully disbursed</div>
              <!--end::Description-->
            </div>
            <!--end::Content-->
            <!--begin::Link-->
            <img alt="Logo" style="height: 85px;border-radius: 30px;" src="assets/media/misc/congo3.png">
            <!--end::Link-->
          </div>
          <!--end::Highlight-->
        </div>
        <!--end::Container-->
      </div>
      <!--end::Testimonials Section-->
      <!--begin::Footer Section-->
      <div class="mb-0">
        <!--begin::Curve top-->
        <div class="landing-curve landing-dark-color">
          <svg viewBox="15 -1 1470 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M1 48C4.93573 47.6644 8.85984 47.3311 12.7725 47H1489.16C1493.1 47.3311 1497.04 47.6644 1501 48V47H1489.16C914.668 -1.34764 587.282 -1.61174 12.7725 47H1V48Z" fill="currentColor"></path>
          </svg>
        </div>

        <!--end::Curve top-->
        <!--begin::Wrapper-->
        <div class="landing-dark-bg pt-20" style="background: white !important;">
          <!--begin::Container-->
          <div class="container">
            <!--begin::Row-->
            <div class="row py-10 py-lg-20">
              <!--begin::Col-->
              <div class="col-lg-6 pe-lg-16 mb-10 mb-lg-0">
                <!--begin::Block-->
                <div class="notice d-flex bg-light-warning rounded border-warning border border-dashed p-6 mb-10">
                <!--begin::Icon-->
                <img alt="Logo" style="height: 65px;border-radius: 30px;" src="assets/media/misc/first.png">
                <!--end::Icon-->
                    <%
                    String message= request.getAttribute("message")==null?"": request.getAttribute("message").toString();
                        String[] messages = message.split("\\|");
                    %>
                <!--begin::Wrapper-->
                <div class=" " style="word-wrap: break-word !important;width:85% !important;">
                  <!--begin::Content-->
                  <div class="fw-semibold">
                    <h4 class="text-gray-900 fw-bold">We need your attention!</h4>
                    <div class="fs-6 text-gray-700"><%=messages[0]%>
                        </div>
                  </div>
                  <!--end::Content-->
                </div>
                <!--end::Wrapper-->
              </div>
                <!--end::Block-->
                <!--begin::Block-->
                <div class="notice d-flex bg-light-warning rounded border-warning border border-dashed p-6 mb-10">
                <!--begin::Icon-->
                <img alt="Logo" style="height: 65px;border-radius: 30px;" src="assets/media/misc/trophy.png">
                <!--end::Icon-->
                <!--begin::Wrapper-->
                <div class="d-flex flex-stack flex-grow-1">
                  <!--begin::Content-->
                  <div class="fw-semibold">
                    <h4 class="text-gray-900 fw-bold">Total Disbursements MileStone</h4>
<%--                    <div class="fs-6 text-gray-700">Drive forward as new dreams wait on the<a class="fw-bold" href="../../demo10/dist/account/billing.html"> Horizon</a>.</div>--%>
                    <div class="fs-6 text-gray-700"><%=messages[1]%></div>
                  </div>
                  <!--end::Content-->
                </div>
                <!--end::Wrapper-->
              </div>
                <!--end::Block-->
              </div>
              <!--end::Col-->
              <!--begin::Col-->
              <div class="col-lg-6 ps-lg-16">
                <!--begin::Navs-->
                <div class="d-flex justify-content-center">
                  <!--begin::Links-->

                  <!--end::Links-->
                  <!--begin::Links-->
                  <img alt="Logo" style="height: 330px;border-radius: 30px;" src="assets/media/misc/car6.gif">
                  <!--end::Links-->
                </div>
                <!--end::Navs-->
              </div>
              <!--end::Col-->
            </div>
            <!--end::Row-->

              <div class="d-flex justify-content-center">
                  <a href="markAsRead" class="btn btn-light "> <i class="ki-outline ki-home-1 fs-2"></i>Home</a>
              </div>
          </div>
          <!--end::Container-->
          <!--begin::Separator-->
<%--          <div class="landing-dark-separator"></div>--%>
          <!--end::Separator-->
          <!--begin::Container-->
          </div>
          <!--end::Container-->
        </div>
        <!--end::Wrapper-->
      </div>
      <!--end::Footer Section-->
      <!--begin::Scrolltop-->
      <div id="kt_scrolltop" class="scrolltop" data-kt-scrolltop="true">
        <i class="ki-duotone ki-arrow-up">
          <span class="path1"></span>
          <span class="path2"></span>
        </i>
      </div>
      <!--end::Scrolltop-->
    </div>
    <!--end::Root-->
    <!--end::Main-->
    <!--begin::Scrolltop-->
    <div id="kt_scrolltop" class="scrolltop" data-kt-scrolltop="true">
      <i class="ki-duotone ki-arrow-up">
        <span class="path1"></span>
        <span class="path2"></span>
      </i>
    </div>
    <!--end::Scrolltop-->
    <!--begin::Javascript-->
    <script>var hostUrl = "assets/";</script>
    <!--begin::Global Javascript Bundle(mandatory for all pages)-->
    <script src="assets/plugins/global/plugins.bundle.js"></script>
    <script src="assets/js/scripts.bundle.js"></script>
    <!--end::Global Javascript Bundle-->
    <!--begin::Vendors Javascript(used for this page only)-->
    <script src="assets/plugins/custom/fslightbox/fslightbox.bundle.js"></script>
    <script src="assets/plugins/custom/typedjs/typedjs.bundle.js"></script>
    <!--end::Vendors Javascript-->
    <!--begin::Custom Javascript(used for this page only)-->
    <script src="assets/js/custom/landing.js"></script>
    <script src="assets/js/custom/pages/pricing/general.js"></script>
    <!--end::Custom Javascript-->
    <!--end::Javascript-->
  
  
    <svg id="SvgjsSvg1001" width="2" height="0" xmlns="http://www.w3.org/2000/svg" version="1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:svgjs="http://svgjs.dev" style="overflow: hidden; top: -100%; left: -100%; position: absolute; opacity: 0;"><defs id="SvgjsDefs1002"></defs><polyline id="SvgjsPolyline1003" points="0,0"></polyline><path id="SvgjsPath1004" d="M0 0 "></path></svg></body>
	<!--end::Body-->
</html>