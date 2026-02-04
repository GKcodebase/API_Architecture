package com.aquaworld.grpc.service.impl;

import com.aquaworld.grpc.product.ProductServiceGrpc;
import com.aquaworld.grpc.product.Product;
import com.aquaworld.grpc.product.ProductRequest;
import com.aquaworld.grpc.product.ProductListRequest;
import com.aquaworld.grpc.product.ProductListResponse;
import com.aquaworld.grpc.product.GetProductRequest;
import com.aquaworld.grpc.product.CreateProductRequest;
import com.aquaworld.grpc.product.UpdateProductRequest;
import com.aquaworld.grpc.product.UpdateStockRequest;
import com.aquaworld.grpc.product.DeleteProductRequest;
import com.aquaworld.grpc.product.StreamProductsRequest;
import com.aquaworld.grpc.exception.GrpcExceptionHandler;
import com.aquaworld.grpc.service.ProductService;
import com.aquaworld.grpc.util.Constants;
import com.aquaworld.grpc.PageInfo;
import com.aquaworld.grpc.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

/**
 * gRPC Product Service Implementation
 * Implements ProductService defined in product.proto
 */
@GrpcService
@RequiredArgsConstructor
public class GrpcProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(GrpcProductServiceImpl.class);

    private final ProductService productService;

    @Override
    public void getProduct(ProductRequest request, StreamObserver<Product> responseObserver) {
        try {
            log.info("Get product request: {}", request.getProductId());

            var product = productService.getProduct(request.getProductId());
            var response = mapProductToProto(product);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Get product failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void listProducts(ProductListRequest request, StreamObserver<ProductListResponse> responseObserver) {
        try {
            log.info("List products request - page: {}, pageSize: {}", request.getPage(), request.getPageSize());

            List<com.aquaworld.grpc.model.Product> products;

            if (request.getCategory() != null && !request.getCategory().isEmpty()) {
                if (request.getSearchQuery() != null && !request.getSearchQuery().isEmpty()) {
                    products = productService.searchProductsByCategoryAndQuery(request.getCategory(), request.getSearchQuery());
                } else {
                    products = productService.getProductsByCategory(request.getCategory());
                }
            } else if (request.getSearchQuery() != null && !request.getSearchQuery().isEmpty()) {
                products = productService.searchProducts(request.getSearchQuery());
            } else {
                products = productService.getAllProducts();
            }

            int page = request.getPage() > 0 ? request.getPage() : Constants.DEFAULT_PAGE;
            int pageSize = request.getPageSize() > 0 ? request.getPageSize() : Constants.DEFAULT_PAGE_SIZE;
            pageSize = Math.min(pageSize, Constants.MAX_PAGE_SIZE);

            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, products.size());

            List<com.aquaworld.grpc.model.Product> paginatedProducts = products.subList(startIndex, endIndex);

            var pageInfo = PageInfo.newBuilder()
                    .setPage(page)
                    .setPageSize(pageSize)
                    .setTotalItems(products.size())
                    .setTotalPages((products.size() + pageSize - 1) / pageSize)
                    .build();

            var response = ProductListResponse.newBuilder()
                    .addAllProducts(paginatedProducts.stream().map(this::mapProductToProto).toList())
                    .setPageInfo(pageInfo)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("List products failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<Product> responseObserver) {
        try {
            log.info("Create product request: {}", request.getName());

            var product = productService.createProduct(
                    request.getName(),
                    request.getDescription(),
                    request.getPrice(),
                    request.getStockQuantity(),
                    request.getCategory(),
                    String.join(",", request.getTagsList()),
                    request.getImageUrl()
            );

            var response = mapProductToProto(product);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Create product failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<Product> responseObserver) {
        try {
            log.info("Update product request: {}", request.getProductId());

            var product = productService.updateProduct(
                    request.getProductId(),
                    request.getName(),
                    request.getDescription(),
                    request.getPrice(),
                    request.getStockQuantity(),
                    request.getCategory(),
                    String.join(",", request.getTagsList()),
                    request.getImageUrl()
            );

            var response = mapProductToProto(product);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Update product failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void updateStock(UpdateStockRequest request, StreamObserver<Product> responseObserver) {
        try {
            log.info("Update stock request: {} - quantity change: {}", request.getProductId(), request.getQuantityChange());

            var product = productService.updateStock(request.getProductId(), request.getQuantityChange());
            var response = mapProductToProto(product);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Update stock failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void deleteProduct(ProductRequest request, StreamObserver<com.aquaworld.grpc.ApiResponse> responseObserver) {
        try {
            log.info("Delete product request: {}", request.getProductId());

            productService.deleteProduct(request.getProductId());

            var response = com.aquaworld.grpc.ApiResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Product deleted successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Delete product failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    @Override
    public void streamProducts(Empty request, StreamObserver<Product> responseObserver) {
        try {
            log.info("Stream products request");

            var products = productService.getAllProducts();

            for (var product : products) {
                responseObserver.onNext(mapProductToProto(product));
            }

            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Stream products failed: {}", e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handle(e));
        }
    }

    private Product mapProductToProto(com.aquaworld.grpc.model.Product product) {
        return Product.newBuilder()
                .setProductId(product.getProductId())
                .setName(product.getName())
                .setDescription(product.getDescription() != null ? product.getDescription() : "")
                .setPrice(product.getPrice())
                .setStockQuantity(product.getStockQuantity())
                .setCategory(product.getCategory())
                .addAllTags(product.getTags() != null ? List.of(product.getTags().split(",")) : List.of())
                .setImageUrl(product.getImageUrl() != null ? product.getImageUrl() : "")
                .setCreatedAt(product.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().getEpochSecond())
                .build();
    }
}
