(function() {
  function collectPerformanceMetrics() {
    const navigation = performance.getEntriesByType("navigation")[0];
    const paint = performance.getEntriesByType("paint");

    const metrics = {
      pageLoadTime: navigation.loadEventEnd - navigation.startTime,
      domContentLoaded: navigation.domContentLoadedEventEnd - navigation.startTime,
      firstPaint: paint.find(entry => entry.name === "first-paint")?.startTime,
      firstContentfulPaint: paint.find(entry => entry.name === "first-contentful-paint")?.startTime,
      url: window.location.href,
      timestamp: new Date().toISOString(),
      resources: getResourceMetrics()
    };

    return metrics;
  }

  function getResourceMetrics() {
    return performance.getEntriesByType("resource").map(resource => ({
      name: resource.name,
      type: resource.initiatorType,
      duration: resource.duration,
      resource_size: resource.transferSize,
      startTime: resource.startTime
    }))
    .sort((a, b) => b.duration - a.duration); // Sort by duration, longest first
  }

  function sendMetricsToServer(metrics) {
    fetch('metricsapi/log-performance', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(metrics),
    })
    .then(response => console.log('Performance metrics sent successfully'))
    .catch(error => console.error('Error sending performance metrics:', error));
  }

  window.addEventListener('load', function() {
    setTimeout(function() {
      const metrics = collectPerformanceMetrics();
      sendMetricsToServer(metrics);
    }, 0);
  });
})();